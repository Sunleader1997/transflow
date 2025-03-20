package org.sunyaxing.transflow.transflowapp.config;

import org.pf4j.PluginManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sunyaxing.transflow.transflowapp.factory.TransFlowExtensionFactory;
import org.sunyaxing.transflow.transflowapp.factory.TransFlowPluginManager;

@Configuration
@EnableConfigurationProperties()
public class PluginConfig {
    @Bean(name = "transFlowPluginManager")
    public PluginManager transFlowPluginManager() {
        return new TransFlowPluginManager(new TransFlowExtensionFactory());
    }
}
