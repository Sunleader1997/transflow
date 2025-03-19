package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

public abstract class TransFlowFilter extends ExtensionLifecycle {

    protected ExtensionContext extensionContext;

    public TransFlowFilter(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }
}
