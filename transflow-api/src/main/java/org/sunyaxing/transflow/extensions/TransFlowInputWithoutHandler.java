package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.function.Function;

public abstract class TransFlowInputWithoutHandler extends TransFlowInput<String> {
    public TransFlowInputWithoutHandler(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public Function<String, HandleData> parseHandleToConsumer(String handleId, String handle) {
        return null;
    }
}
