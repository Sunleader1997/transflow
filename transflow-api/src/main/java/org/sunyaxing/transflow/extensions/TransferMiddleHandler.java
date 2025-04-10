package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

/**
 * 把数据转换成 R
 */
public abstract class TransferMiddleHandler<R> extends ExtensionLifecycle<TransData, R> {
    public TransferMiddleHandler(ExtensionContext extensionContext) {
        super(extensionContext);
    }
}
