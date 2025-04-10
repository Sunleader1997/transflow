package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

import java.util.Collections;
import java.util.List;

/**
 * 一个 input 就是一个虚拟线程，用于从外部获取数据
 */
public abstract class TransFlowInput<T,R> extends ExtensionLifecycle<T,R> {

    public ExtensionContext extensionContext;

    public TransFlowInput(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    public abstract void commit(HandleData handleData);

    public abstract HandleData dequeue();

    @Override
    public List<HandleData> exec(HandleData handleData) {
        return Collections.singletonList(handleData);
    }
}
