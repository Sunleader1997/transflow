package org.sunyaxing.transflow.transflowapp.config.porperties;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;


@Data
public class FilterProperty {
    private String pluginId;
    @NestedConfigurationProperty
    private JSONObject config;
}
