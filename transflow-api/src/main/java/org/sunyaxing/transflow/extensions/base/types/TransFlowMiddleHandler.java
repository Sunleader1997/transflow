package org.sunyaxing.transflow.extensions.base.types;

import org.sunyaxing.transflow.extensions.base.ExecutableExt;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

/**
 * 中间处理器
 */
public abstract class TransFlowMiddleHandler<T, FR, R> extends ExtensionLifecycle<T, FR> implements ExecutableExt<T, R> {
    public TransFlowMiddleHandler(ExtensionContext extensionContext) {
        super(extensionContext);
    }
}
