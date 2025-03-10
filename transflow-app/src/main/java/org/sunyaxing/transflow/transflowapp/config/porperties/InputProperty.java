package org.sunyaxing.transflow.transflowapp.config.porperties;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;
import java.util.Properties;

@Data
public class InputProperty {
    private String pluginId;
    @NestedConfigurationProperty
    private Properties config;
    @NestedConfigurationProperty
    private List<FilterProperty> filters;
    @NestedConfigurationProperty
    private List<OutputsProperty> outputs;
}
