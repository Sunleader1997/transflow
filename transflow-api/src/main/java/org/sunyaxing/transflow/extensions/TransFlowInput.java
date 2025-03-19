package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

import java.util.List;

/**
 * 一个 input 就是一个虚拟线程，用于从外部获取数据
 */
public abstract class TransFlowInput extends ExtensionLifecycle{

    public ExtensionContext extensionContext;

    public TransFlowInput(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }

    @Override
    protected List<TransData> execDatas(String handleValue, List<TransData> data) {
        return data;
    }

    public void commit(HandleData handleData) {
    }

    public abstract HandleData dequeue();
}
