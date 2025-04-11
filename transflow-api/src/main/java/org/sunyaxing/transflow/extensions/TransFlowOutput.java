package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

import java.util.List;

public abstract class TransFlowOutput<R> extends ExtensionLifecycle<TransData, R> {

    public TransFlowOutput(ExtensionContext extensionContext) {
        super(extensionContext);
    }
}
