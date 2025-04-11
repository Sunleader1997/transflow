package org.sunyaxing.transflow.extensions.base;

import com.alibaba.fastjson2.JSONObject;
import org.pf4j.ExtensionPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.common.Handle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class ExtensionLifecycle<T, R> implements ExtensionPoint {

    private static final Logger log = LoggerFactory.getLogger(ExtensionLifecycle.class);
    // 使用LinkedHashMap按顺序执行处理器
    protected final LinkedHashMap<String, Function<T, R>> handlerMap;

    public ExtensionLifecycle(ExtensionContext extensionContext) {
        this.handlerMap = new LinkedHashMap<>();
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
            Function<T, R> handler = parseHandleToConsumer(handle.getId(), handle.getValue());
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
    public abstract Function<T, R> parseHandleToConsumer(String handleId, String handleValue);

    /**
     * 接收上一个节点来的数据并处理
     *
     * @return 返回给下一个节点的数据
     */
    public abstract Optional<HandleData> exec(HandleData handleData);

    public abstract void destroy();

}
