package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.handlers.Handler;

import java.util.Collections;
import java.util.List;

public abstract class TransFlowOutputWithHandler<R> extends TransFlowOutput<R>{
    public TransFlowOutputWithHandler(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public List<HandleData> exec(HandleData handleData) {
        Handler<List<TransData>, R> handler = this.handlerMap.get(handleData.getHandleId());
        R res = handler.resolve(handleData.getTransData());
        execData(res);
        return Collections.emptyList();
    }

    protected abstract void execData(R data);
}
