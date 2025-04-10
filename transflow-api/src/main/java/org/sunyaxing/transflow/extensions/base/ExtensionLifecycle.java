package org.sunyaxing.transflow.extensions.base;

import com.alibaba.fastjson2.JSONObject;
import org.pf4j.ExtensionPoint;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.handlers.Handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ExtensionLifecycle<T,R> implements ExtensionPoint {

    protected final Map<String, Handler<T,R>> handlerMap;

    public ExtensionLifecycle(ExtensionContext extensionContext) {
        this.handlerMap = new HashMap<>();
    }

    /**
     * 插件初始化
     */
    public void init(JSONObject config, List<Handle> handles) {
        this.initForHandle(config, handles);
        this.afterInitHandler(config, handles);
    }

    /**
     * 从配置中获取handle的id和value，并保存到handleMap中
     */
    protected void initForHandle(JSONObject config, List<Handle> handles) {
        for (Handle handle : handles) {
            Handler<T,R> handler = parseHandleToHandler(handle.getId(), handle.getValue());
            this.handlerMap.put(handle.getId(), handler);
        }
    }

    /**
     * 从配置中获取handle的id和value，并保存到handleMap中
     */
    protected abstract void afterInitHandler(JSONObject config, List<Handle> handles);
    /**
     * 每个实例化的插件都要根据分配的handle初始化自己的handler
     */
    public abstract Handler<T,R> parseHandleToHandler(String handleId, String handle);

    /**
     * 接收上一个节点来的数据并处理
     * @return 返回给下一个节点的数据
     */
    public abstract List<HandleData> exec(HandleData handleData);

    public abstract void destroy();

}
