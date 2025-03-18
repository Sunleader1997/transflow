package org.sunyaxing.transflow.transflowapp.services.bos;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.transflowapp.common.TransFlowTypeEnum;
import org.sunyaxing.transflow.transflowapp.config.JobConfigProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class NodeBo {
    private String id;
    private String jobId;
    private String name;
    private TransFlowTypeEnum nodeType;
    private String pluginId;
    private JSONObject config;
    private Integer x;
    private Integer y;
    private List<JobConfigProperties> properties;
    private List<Handle> handles;
    private Map<String, String> handlesCache;

    public String getHandle(String handleId) {
        if (handlesCache == null) {
            handlesCache = new HashMap<>();
        }
        if (handlesCache.containsKey(handleId)) {
            return handlesCache.get(handleId);
        } else {
            String value = handles.stream()
                    .filter(handle -> handle.getId().equals(handleId))
                    .findFirst()
                    .map(Handle::getValue)
                    .orElse(null);
            handlesCache.put(handleId, value);
            return value;
        }
    }
}
