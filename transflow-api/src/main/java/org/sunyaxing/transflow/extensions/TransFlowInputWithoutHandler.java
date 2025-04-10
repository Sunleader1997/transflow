package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.handlers.Handler;

public abstract class TransFlowInputWithoutHandler extends TransFlowInput<String,Void>{
    public TransFlowInputWithoutHandler(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public Handler<String, Void> parseHandleToHandler(String handleId, String handle) {
        return null;
    }
}
