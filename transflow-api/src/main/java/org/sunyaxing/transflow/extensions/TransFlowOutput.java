package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

public abstract class TransFlowOutput implements ExtensionLifecycle {
    protected ExtensionContext extensionContext;

    protected TransFlowOutput(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }

    public abstract void output(Object data);
}
