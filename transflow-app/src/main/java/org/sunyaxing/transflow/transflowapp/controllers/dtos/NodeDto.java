package org.sunyaxing.transflow.transflowapp.controllers.dtos;

import lombok.Data;
import org.sunyaxing.transflow.common.ChainStatusEnum;
import org.sunyaxing.transflow.transflowapp.common.TransFlowTypeEnum;

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
        private ChainStatusEnum status;
        private TransFlowTypeEnum nodeType;
        private Properties config;
    }
}
