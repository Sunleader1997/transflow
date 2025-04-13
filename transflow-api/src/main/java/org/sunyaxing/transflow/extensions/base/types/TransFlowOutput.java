package org.sunyaxing.transflow.extensions.base.types;

import org.sunyaxing.transflow.extensions.base.ExecutableExt;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

/**
 * 输出处理器
 */
public abstract class TransFlowOutput<T, FR> extends ExtensionLifecycle<T, FR> implements ExecutableExt<T, FR> {

    public TransFlowOutput(ExtensionContext extensionContext) {
        super(extensionContext);
    }
}
