package org.sunyaxing.transflow.transflowapp.controllers.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class JobDto {
    private Long id;
    private String name;
    private String description;
    private Long inputId;
    private Date updateTime;
}
