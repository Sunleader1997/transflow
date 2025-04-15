package org.sunyaxing.transflow.transflowapp.config.porperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "route")
public class RouteConfig {
    private Boolean enable;
    private String jobId;
    private List<Route> routes;
}
