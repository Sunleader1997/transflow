package org.sunyaxing.transflow.transflowapp.controllers.dtos;

import lombok.Data;
import org.sunyaxing.transflow.common.ChainStatusEnum;
import org.sunyaxing.transflow.transflowapp.common.TransFlowTypeEnum;

import java.util.Properties;

@Data
public class NodeDto {
    private Long id;
    private Long jobId;
    private String name;
    private TransFlowTypeEnum nodeType;
    private ChainStatusEnum status;
    private String pluginId;
    private Properties config;
}
