package org.sunyaxing.transflow.transflowapp.config.porperties;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

@Data
public class InputProperty {
    private String pluginId;
    @NestedConfigurationProperty
    private JSONObject config;
    @NestedConfigurationProperty
    private List<FilterProperty> filters;
    @NestedConfigurationProperty
    private List<OutputsProperty> outputs;
}
