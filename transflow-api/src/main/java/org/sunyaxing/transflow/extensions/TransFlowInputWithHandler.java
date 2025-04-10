package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.List;

/**
 * 可以配置handle的input
 * 适合多个handle的场景
 */
public abstract class TransFlowInputWithHandler<T,R> extends TransFlowInput<T,R> {

    public TransFlowInputWithHandler(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    public abstract List<HandleData> handleDequeue();

    @Override
    public HandleData dequeue() {
        return null;
    }
}
