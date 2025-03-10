package org.sunyaxing.transflow.transflowapp.config.porperties;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Properties;

@Data
public class OutputsProperty {
    private String pluginId;
    @NestedConfigurationProperty
    private Properties config;
}
