package org.sunyaxing.transflow.transflowapp.config.porperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

@Data
@ConfigurationProperties("transflow")
public class TransFlowProperties {
    @NestedConfigurationProperty
    private List<InputProperty> inputs;
}
