package org.sunyaxing.transflow.transflowapp.services.bos;

import lombok.Data;

import java.util.Date;

@Data
public class JobBo {
    private String id;
    private String name;
    private String description;
    private String inputId;
    private Date updateTime;
    private Boolean restart;
}
