package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.Optional;
import java.util.function.Function;

public abstract class TransFlowOutputWithHandler<R> extends TransFlowOutput<R> {
    public TransFlowOutputWithHandler(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public Optional<HandleData> exec(HandleData handleData) {
        Function<TransData, R> handler = this.handlerMap.get(handleData.getHandleId());
        R res = handler.apply(handleData.getTransData());
        execData(res);
        return Optional.of(handleData);
    }

    protected abstract void execData(R data);
}
