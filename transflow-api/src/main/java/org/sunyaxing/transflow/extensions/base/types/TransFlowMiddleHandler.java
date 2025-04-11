package org.sunyaxing.transflow.extensions.base.types;

import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

/**
 * 中间处理器
 */
public abstract class TransFlowMiddleHandler<T, R, FR> extends ExtensionLifecycle<T, R, FR> {
    public TransFlowMiddleHandler(ExtensionContext extensionContext) {
        super(extensionContext);
    }
}
