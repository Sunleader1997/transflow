package org.sunyaxing.transflow.transflowapp.services.bos;

import lombok.Data;

import java.util.Date;

@Data
public class JobBo {
    private Long id;
    private String name;
    private String description;
    private Long inputId;
    private Date updateTime;
}
