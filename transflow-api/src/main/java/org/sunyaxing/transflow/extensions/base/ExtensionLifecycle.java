package org.sunyaxing.transflow.extensions.base;

import com.alibaba.fastjson2.JSONObject;
import org.pf4j.ExtensionPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

/**
 * ExtensionLifecycle 插件的生命周期
 *
 * @param <FT> 处理器接收一个 HandleData<FT> 的消息
 * @param <FR> 处理器处理 <FT> 返回一个 <R> 的消息
 */
public abstract class ExtensionLifecycle<FT, FR> implements ExtensionPoint {

    private static final Logger log = LoggerFactory.getLogger(ExtensionLifecycle.class);
    // 使用LinkedHashMap按顺序执行处理器
    // 处理器 接收一个 TransData<T> 的数据 以提供数据修改可行性
    protected final LinkedHashMap<String, Function<TransData<FT>, FR>> handlerMap;
    protected final HashMap<String, String> valueToHandleId;

    public ExtensionLifecycle(ExtensionContext extensionContext) {
        this.handlerMap = new LinkedHashMap<>();
        this.valueToHandleId = new LinkedHashMap<>();
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
            Function<TransData<FT>, FR> handler = parseHandleToConsumer(handle.getId(), handle.getValue());
            this.handlerMap.put(handle.getId(), handler);
            this.valueToHandleId.put(handle.getValue(), handle.getId());
        }
    }

    public String findHandleIdByValue(String handleId) {
        return this.valueToHandleId.get(handleId);
    }

    /**
     * 从配置中获取handle的id和value，并保存到handleMap中
     */
    protected abstract void afterInitHandler(JSONObject config, List<Handle> handles);

    /**
     * 每个实例化的插件都要根据分配的handle初始化自己的handler
     */
    public abstract Function<TransData<FT>, FR> parseHandleToConsumer(String handleId, String handleValue);


    public abstract void destroy();

}
