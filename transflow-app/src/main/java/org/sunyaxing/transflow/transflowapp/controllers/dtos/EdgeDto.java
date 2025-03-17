package org.sunyaxing.transflow.transflowapp.controllers.dtos;

import lombok.Data;

@Data
public class EdgeDto {
    private String id;
    private String source;
    private String sourceHandle;
    private String target;
    private String targetHandle;
    private Boolean animated = true;
    private String type = "special";
}
