package org.sunyaxing.transflow.extensions.base;

import com.alibaba.fastjson2.JSONObject;
import org.pf4j.ExtensionPoint;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ExtensionLifecycle implements ExtensionPoint {

    protected Map<String, String> handleMap;

    public void initForHandle(JSONObject config, List<Handle> handles) {
        this.handleMap = handles.stream().collect(Collectors.toMap(Handle::getId, Handle::getValue));
    }

    /**
     * 插件初始化
     * @param config
     * @param handles
     */
    public void init(JSONObject config, List<Handle> handles) {
        this.initForHandle(config, handles);
        this.initSelf(config, handles);
    }

    /**
     * 自定义插件需要自己实现的初始化方法
     * @param config
     * @param handles
     */
    protected abstract void initSelf(JSONObject config, List<Handle> handles);

    /**
     * chain 需要调用的消费方法
     * @param handleId
     * @param data
     */
    public List<TransData> exec(String handleId, List<TransData> data) {
        String handValue = this.handleMap.get(handleId);
        return execDatas(handValue, data);
    }

    /**
     * 自定义插件需要自己实现的消息消费逻辑
     * @param handleValue
     * @param data
     */
    protected abstract List<TransData> execDatas(String handleValue, List<TransData> data);

    public abstract void destroy();

    public Long getRecNumb() {
        return 0L;
    }

    public Long getSendNumb() {
        return 0L;
    }
    // 获取剩余未消费的数据量
    public Long getRemainingDataSize(){
        return 0L;
    }
}
