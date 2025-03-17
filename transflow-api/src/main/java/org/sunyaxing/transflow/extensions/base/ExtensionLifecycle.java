package org.sunyaxing.transflow.extensions.base;

import com.alibaba.fastjson2.JSONObject;
import org.pf4j.ExtensionPoint;
import org.sunyaxing.transflow.TransData;

import java.util.List;

public interface ExtensionLifecycle extends ExtensionPoint {
    public void init(JSONObject config);

    public abstract List<TransData> execDatas(String handle, List<TransData> data);

    public void destroy();
}
