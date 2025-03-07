package org.sunyaxing.transflow.extensions;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class TransFlowInput implements ExtensionLifecycle {
    protected BlockingDeque<String> inputQueue;
    protected ExtensionContext extensionContext;

    public TransFlowInput(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }

    public void setMaxBatchSize(int batchSize) {
        this.inputQueue = new LinkedBlockingDeque<>(batchSize);
    }

    /**
     * 获取单个输入数据
     *
     * @return
     */
    public abstract String dequeue();
}
