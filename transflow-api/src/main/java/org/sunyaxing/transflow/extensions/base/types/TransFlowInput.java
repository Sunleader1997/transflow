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
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Stream;

/**
 * 一个 input插件
 * input 生产数据（需要子类自己实现），这个数据是 FT
 * 调用 input 的 handler 接收 input 生产的 FT 数据，将其转换成队列内的 HandleData<FR> 数据（需要子类自己实现）, 并塞到队列
 * 主线程会从队列内批量拉取 HandleData<FR> 数据去消费
 */
public abstract class TransFlowInput<FT, FR> extends ExtensionLifecycle<FT, FR> {
    // 存储 input 所生产的数据
    private final BlockingDeque<HandleData<FR>> blockingDeque;

    public TransFlowInput(ExtensionContext extensionContext) {
        super(extensionContext);
        this.blockingDeque = new LinkedBlockingDeque<>(10000);
    }

    public abstract void commit(HandleData<FR> handleData);

    /**
     * 接受一个 HandleData, 交给 handler 处理
     * 处理后返回的数据将会塞进队列
     */
    public void handle(String handleId, TransData<FT> transData) {
        FR handlerReturn = this.handlerMap.get(handleId).apply(transData);
        HandleData<FR> handleData = new HandleData<>(handleId, new TransData<>(transData.getOffset(), handlerReturn));
        this.putQueueLast(handleData);
    }

    /**
     * 主线程会调用此方法批量拉取数据
     */
    public Publisher<HandleData> dequeue() {
        try {
            List<HandleData<FR>> dataList = new ArrayList<>();
            // 一次消费多条信息
            blockingDeque.drainTo(dataList, 1000);
            if (!dataList.isEmpty()) {
                Stream<HandleData<FR>> stream = dataList.stream();
                return Flux.fromStream(stream);
            } else {
                return Mono.empty();
            }
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    /**
     * 插件需要调用此方法推送 HandleData<T> 数据
     */
    public void putQueueLast(HandleData<FR> data) {
        try {
            blockingDeque.putLast(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
