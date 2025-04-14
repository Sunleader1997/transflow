package org.sunyaxing.transflow.extensions.base.typesimpl;

import com.alibaba.fastjson2.JSONObject;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.types.TransFlowOutput;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * 数据源需要指定当前处理器处理
 * 处理器负责将数据持久化 或者转成可以持久化的格式
 */
public abstract class TransFlowOutputWithHandler<T, FR> extends TransFlowOutput<T, FR> {

    private static final Logger log = LoggerFactory.getLogger(TransFlowOutputWithHandler.class);
    protected final AtomicLong rec = new AtomicLong(0);
    // 存储 input 所生产的数据
    private final BlockingDeque<FR> blockingDeque;
    private final Flux<List<FR>> flux;
    private Disposable disposable;

    public TransFlowOutputWithHandler(ExtensionContext extensionContext) {
        super(extensionContext);
        this.blockingDeque = new LinkedBlockingDeque<>(10000);
        this.flux = Flux.defer(this::batchDequeue).repeat();
    }

    @Override
    public Optional<HandleData<FR>> exec(HandleData<T> handleData) {
        rec.incrementAndGet();
        Function<TransData<T>, FR> handler = this.handlerMap.get(handleData.getHandleId());
        FR res = handler.apply(handleData.getTransData());
        try {
            this.blockingDeque.putLast(res);
        } catch (Exception e) {
            log.error("数据队列 putLast 出现异常", e);
        }
        return Optional.empty();
    }

    @Override
    protected void initForHandle(JSONObject config, List<Handle> handles) {
        super.initForHandle(config, handles);
        this.disposable = Flux.<List<FR>>from(this.flux)
                .subscribeOn(Schedulers.newSingle("batchOuter"))
                .subscribe(this::batchExec);
    }

    @Override
    public void destroy() {
        this.disposable.dispose();
    }

    /**
     * 主线程会调用此方法批量拉取数据
     */
    public Publisher<List<FR>> batchDequeue() {
        try {
            List<FR> dataList = new ArrayList<>();
            // 一次消费多条信息
            blockingDeque.drainTo(dataList, 1000);
            if (!dataList.isEmpty()) {
                return Mono.just(dataList);
            } else {
                return Mono.empty();
            }
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    protected abstract void batchExec(List<FR> dataList);

}
