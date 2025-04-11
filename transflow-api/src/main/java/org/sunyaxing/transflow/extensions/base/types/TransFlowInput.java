package org.sunyaxing.transflow.extensions.base.types;

import org.reactivestreams.Publisher;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Stream;

/**
 * 一个 input插件
 *
 * @param <T> 接收一个 HandleData<T> 的消息
 * @param <R> 转换成 HandleData<R> 的消息
 * @param <R> 处理器处理 <T> 返回一个 <R> 的消息
 */
public abstract class TransFlowInput<T, R> extends ExtensionLifecycle<T, R, R> {
    // 存储 input 所生产的数据
    private final BlockingDeque<HandleData<T>> blockingDeque;

    public TransFlowInput(ExtensionContext extensionContext) {
        super(extensionContext);
        this.blockingDeque = new LinkedBlockingDeque<>(10000);
    }

    public abstract void commit(HandleData<R> handleData);

    /**
     * 主线程会调用此方法批量拉取数据
     * 调用 parseRToHandleData 将数据从 HandleData<T> 转换成 HandleData<R>
     */
    public Publisher<HandleData<R>> dequeue() {
        try {
            List<HandleData<T>> dataList = new ArrayList<>();
            // 一次消费多条信息
            blockingDeque.drainTo(dataList, 1000);
            if (!dataList.isEmpty()) {
                Stream<HandleData<R>> stream = dataList.stream().map(tHandleData -> {
                    String handleId = tHandleData.getHandleId();
                    R res = this.handlerMap.get(handleId).apply(tHandleData.getTransData());
                    return new HandleData<R>(handleId, new TransData<R>(tHandleData.getTransData().getOffset(), res));
                });
                return Flux.fromStream(stream);
            } else {
                return Mono.empty();
            }
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    /**
     * 子类实现此方法，将数据转换成 HandleData
     * 以指定该条数据产自哪一个 handler
     */
    protected abstract HandleData<R> parseRToHandleData(HandleData<T> data);

    /**
     * 插件需要调用此方法推送 HandleData<T> 数据
     */
    public void putQueueLast(HandleData<T> data) {
        try {
            blockingDeque.putLast(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<HandleData<R>> exec(HandleData<T> handleData) {
        R res = this.handlerMap.get(handleData.getHandleId()).apply(handleData.getTransData());
        return Optional.of(new HandleData<R>(handleData.getHandleId(), new TransData<R>(handleData.getTransData().getOffset(), res)));
    }
}
