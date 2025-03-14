package org.sunyaxing.transflow.transflowapp.controllers.dtos;

import lombok.Data;

@Data
public class PluginListDto{
    private String id;
    private String type;
    private String state;
    private String description;
    private String version;
    private String provider;
}
