package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

import java.util.List;

/**
 * 一个 input 就是一个虚拟线程，用于从外部获取数据
 */
public abstract class TransFlowInput implements ExtensionLifecycle, Runnable {

    public ExtensionContext extensionContext;

    public TransFlowInput(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }

    public void commit(Long offset){
    }
    // 获取剩余未消费的数据量
    public Long getRemainingDataSize(){
        return 0L;
    }
    public abstract List<TransData> dequeue();
}
