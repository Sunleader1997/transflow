package org.sunyaxing.transflow.extensions;

import org.reactivestreams.Publisher;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Stream;

/**
 * 一个 input 就是一个虚拟线程，用于从外部获取数据
 */
public abstract class TransFlowInput<T> extends ExtensionLifecycle<T, HandleData> {

    public ExtensionContext extensionContext;
    // 存储 input 所生产的数据
    private final BlockingDeque<HandleData> blockingDeque;

    public TransFlowInput(ExtensionContext extensionContext) {
        super(extensionContext);
        this.blockingDeque = new LinkedBlockingDeque<>(10000);
    }

    public abstract void commit(HandleData handleData);

    /**
     * 子类实现此方法，将数据转换成 HandleData
     * 以指定该条数据产自哪一个 handler
     */
    protected abstract HandleData parseRToHandleData(HandleData data);

    /**
     * 主线程会调用此方法批量拉取数据
     */
    public Publisher<HandleData> dequeue() {
        try {
            List<HandleData> dataList = new ArrayList<>();
            // 一次消费多条信息
            blockingDeque.drainTo(dataList, 1000);
            if (!dataList.isEmpty()) {
                Stream<HandleData> stream = dataList.stream().map(this::parseRToHandleData);
                return Flux.fromStream(stream);
            } else {
                return Mono.empty();
            }
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    /**
     * 插件需要调用此方法推送数据
     */
    public void putQueueLast(HandleData data) {
        try {
            blockingDeque.putLast(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<HandleData> exec(HandleData handleData) {
        return Optional.of(handleData);
    }
}
