package org.sunyaxing.transflow.transflowapp.controllers.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class JobDto {
    private String id;
    private String name;
    private String description;
    private String inputId;
    private Date updateTime;
    private Boolean restart;
    private Boolean isRunning;
}
