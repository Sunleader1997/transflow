package org.sunyaxing.transflow.transflowapp.services.bos;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.sunyaxing.transflow.transflowapp.common.TransFlowTypeEnum;
import org.sunyaxing.transflow.transflowapp.config.JobConfigProperties;

import java.util.List;

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
}
