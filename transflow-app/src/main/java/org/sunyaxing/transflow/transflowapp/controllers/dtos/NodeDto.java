package org.sunyaxing.transflow.transflowapp.controllers.dtos;

import lombok.Data;
import org.sunyaxing.transflow.common.ChainStatusEnum;
import org.sunyaxing.transflow.transflowapp.common.TransFlowTypeEnum;
import org.sunyaxing.transflow.transflowapp.config.JobConfigProperties;

import java.util.List;
import java.util.Properties;

@Data
public class NodeDto {
    private String id;
    private String type;
    private Position position;
    private NodeData data;

    @Data
    public static class Position {
        private Integer x;
        private Integer y;
    }

    @Data
    public static class NodeData {
        private String name;
        private String jobId;
        private String pluginId;
        private ChainStatusEnum status;
        private TransFlowTypeEnum nodeType;
        private Properties config;
        private List<JobConfigProperties> properties;
    }
}
