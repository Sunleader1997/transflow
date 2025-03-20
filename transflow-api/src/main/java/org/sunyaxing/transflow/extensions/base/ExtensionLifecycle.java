package org.sunyaxing.transflow.extensions.base;

import com.alibaba.fastjson2.JSONObject;
import org.pf4j.ExtensionPoint;
import org.pf4j.util.StringUtils;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class ExtensionLifecycle implements ExtensionPoint {

    protected LinkedHashMap<String, String> handleMap;

    public void initForHandle(JSONObject config, List<Handle> handles) {
        this.handleMap = new LinkedHashMap<>();
        for (Handle handle : handles) {
            this.handleMap.put(handle.getId(), handle.getValue());
        }
    }

    /**
     * 插件初始化
     *
     * @param config
     * @param handles
     */
    public void init(JSONObject config, List<Handle> handles) {
        this.initForHandle(config, handles);
        this.initSelf(config, handles);
    }

    /**
     * 自定义插件需要自己实现的初始化方法
     *
     * @param config
     * @param handles
     */
    protected abstract void initSelf(JSONObject config, List<Handle> handles);

    /**
     * 过滤器模式
     * 即 所有的handle都会处理HandleData内的数据
     * chain 需要调用的消费方法
     */
    public List<HandleData> exec(HandleData handleData) {
        List<HandleData> handleDatas = new ArrayList<>();
        if (StringUtils.isNullOrEmpty(handleData.getHandleId())) {
            this.handleMap.forEach((k, v) -> {
                List<TransData> data = execDatas(v, handleData.getTransData());
                if (!data.isEmpty()) handleDatas.add(new HandleData(k, data));
            });
        } else {
            String handValue = this.handleMap.get(handleData.getHandleId());
            List<TransData> data = execDatas(handValue, handleData.getTransData());
            if (!data.isEmpty()) handleDatas.add(new HandleData(handleData.getHandleId(), data));
        }
        return handleDatas;
    }

    /**
     * 自定义插件需要自己实现的消息消费逻辑
     *
     * @param handleValue
     * @param data
     */
    protected abstract List<TransData> execDatas(String handleValue, List<TransData> data);

    public abstract void destroy();

}
