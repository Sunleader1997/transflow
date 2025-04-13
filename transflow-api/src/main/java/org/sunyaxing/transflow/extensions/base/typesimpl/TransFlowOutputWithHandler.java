package org.sunyaxing.transflow.extensions.base.typesimpl;

import com.alibaba.fastjson2.JSONObject;
import org.reactivestreams.Publisher;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.types.TransFlowOutput;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    protected final AtomicLong rec = new AtomicLong(0);
    // 存储 input 所生产的数据
    private final BlockingDeque<FR> blockingDeque;
    private final Flux<List<FR>> flux;
    private final Disposable disposable;

    public TransFlowOutputWithHandler(ExtensionContext extensionContext) {
        super(extensionContext);
        this.blockingDeque = new LinkedBlockingDeque<>(10000);
        this.flux = Flux.defer(this::batchDequeue).repeat();
        this.disposable = Flux.<List<FR>>from(this.flux)
                .subscribe(this::batchExec);
    }

    @Override
    public Optional<HandleData<FR>> exec(HandleData<T> handleData) throws InterruptedException {
        rec.incrementAndGet();
        Function<TransData<T>, FR> handler = this.handlerMap.get(handleData.getHandleId());
        FR res = handler.apply(handleData.getTransData());
        this.blockingDeque.putLast(res);
        return Optional.empty();
    }

    @Override
    protected void initForHandle(JSONObject config, List<Handle> handles) {
        super.initForHandle(config, handles);
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
