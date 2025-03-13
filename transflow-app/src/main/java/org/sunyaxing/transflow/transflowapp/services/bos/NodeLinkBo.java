package org.sunyaxing.transflow.transflowapp.services.bos;

import lombok.Data;

@Data
public class NodeLinkBo {
    private Long id;
    private Long sourceId;
    private Long targetId;
}
