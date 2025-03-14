package org.sunyaxing.transflow.transflowapp.controllers.dtos;

import lombok.Data;
import org.sunyaxing.transflow.transflowapp.config.JobConfigProperties;

import java.util.List;

@Data
public class PluginListDto{
    private String id;
    private String type;
    private String state;
    private String description;
    private String version;
    private String provider;
    private List<JobConfigProperties> properties;
}
