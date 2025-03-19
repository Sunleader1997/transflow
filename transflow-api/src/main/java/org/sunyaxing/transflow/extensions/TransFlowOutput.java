package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

public abstract class TransFlowOutput extends ExtensionLifecycle {
    protected ExtensionContext extensionContext;

    public TransFlowOutput(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }
}
