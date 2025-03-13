package org.sunyaxing.transflow.transflowapp.services.bos;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.sunyaxing.transflow.transflowapp.common.TransFlowTypeEnum;

@Data
public class NodeBo {
    private Long id;
    private Long jobId;
    private String name;
    private TransFlowTypeEnum nodeType;
    private String pluginId;
    private JSONObject config;
}
