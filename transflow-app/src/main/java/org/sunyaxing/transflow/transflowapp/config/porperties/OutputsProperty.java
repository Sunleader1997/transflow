package org.sunyaxing.transflow.transflowapp.config.porperties;

import cn.hutool.json.JSONObject;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
public class OutputsProperty {
    private String pluginId;
    @NestedConfigurationProperty
    private JSONObject config;
}
