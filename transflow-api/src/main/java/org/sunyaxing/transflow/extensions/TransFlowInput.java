package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

/**
 * 一个 input 就是一个虚拟线程，用于从外部获取数据
 */
public abstract class TransFlowInput<T> implements ExtensionLifecycle, Runnable {

    public ExtensionContext<T> extensionContext;

    public TransFlowInput(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }

    public void commit(Long offset){
    }
    public void put(TransData<T> transData) {
        try {
            this.extensionContext.inputQueue().put(transData);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public TransData<T> dequeue() {
        return this.extensionContext.dequeue();
    }
}
