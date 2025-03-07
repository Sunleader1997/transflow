package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

/**
 * 一个 input 就是一个虚拟线程，用于从外部获取数据
 */
public abstract class TransFlowInput implements ExtensionLifecycle, Runnable {

    public ExtensionContext extensionContext;

    public TransFlowInput(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }

    public void put(String string) {
        try {
            this.extensionContext.inputQueue.put(string);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
