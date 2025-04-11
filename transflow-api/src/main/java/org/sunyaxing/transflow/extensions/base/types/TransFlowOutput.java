package org.sunyaxing.transflow.extensions.base.types;

import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

/**
 * 输出处理器
 */
public abstract class TransFlowOutput<T, R, FR> extends ExtensionLifecycle<T, R, FR> {

    public TransFlowOutput(ExtensionContext extensionContext) {
        super(extensionContext);
    }
}
