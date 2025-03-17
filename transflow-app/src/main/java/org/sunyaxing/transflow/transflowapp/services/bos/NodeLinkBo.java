package org.sunyaxing.transflow.transflowapp.services.bos;

import lombok.Data;

@Data
public class NodeLinkBo {
    private String id;
    private String sourceId;
    private String sourceHandle;
    private String targetId;
    private String targetHandle;
}
