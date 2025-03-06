package org.sunyaxing.transflow.extensions;

public interface TransFlowInput extends ExtensionLifecycle{
    /**
     * 获取单个输入数据
     * @return
     */
    public String dequeue();
}
