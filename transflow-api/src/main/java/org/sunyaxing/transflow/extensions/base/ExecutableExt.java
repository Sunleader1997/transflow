package org.sunyaxing.transflow.extensions.base;

import org.sunyaxing.transflow.HandleData;

import java.util.Optional;

public interface ExecutableExt<T, R> {
    /**
     * 接收上一个节点来的数据并处理
     * @return 返回给下一个节点的数据
     */
    public abstract Optional<HandleData<R>> exec(HandleData<T> handleData);
}
