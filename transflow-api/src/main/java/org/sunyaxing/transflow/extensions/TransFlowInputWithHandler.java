package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

/**
 * 可以配置handle的input
 * 适合多个handle的场景
 */
public abstract class TransFlowInputWithHandler<T> extends TransFlowInput<T> {
    public TransFlowInputWithHandler(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    protected HandleData parseRToHandleData(HandleData data) {
        return data;
    }
}
