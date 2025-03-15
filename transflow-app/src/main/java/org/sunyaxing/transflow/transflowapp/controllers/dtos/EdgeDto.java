package org.sunyaxing.transflow.transflowapp.controllers.dtos;

import lombok.Data;

@Data
public class EdgeDto {
    private String id;
    private String source;
    private String target;
    private Boolean animated = true;
    private String type = "special";
}
