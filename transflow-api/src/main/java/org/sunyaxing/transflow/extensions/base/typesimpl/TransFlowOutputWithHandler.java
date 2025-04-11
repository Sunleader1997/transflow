package org.sunyaxing.transflow.extensions.base.typesimpl;

import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.types.TransFlowOutput;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * 数据源需要指定当前处理器处理
 * 处理器负责将数据持久化 或者转成可以持久化的格式
 */
public abstract class TransFlowOutputWithHandler<T, R> extends TransFlowOutput<T, R, R> {

    protected final AtomicLong rec = new AtomicLong(0);

    public TransFlowOutputWithHandler(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public Optional<HandleData<R>> exec(HandleData<T> handleData) {
        rec.incrementAndGet();
        Function<TransData<T>, R> handler = this.handlerMap.get(handleData.getHandleId());
        R res = handler.apply(handleData.getTransData());
        HandleData<R> result = new HandleData<R>(handleData.getHandleId(), new TransData<R>(null, res));
        return Optional.of(result);
    }

}
