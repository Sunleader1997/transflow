package org.sunyaxing.transflow.transflowapp.config;

import org.pf4j.PluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sunyaxing.transflow.TransFlowPluginManager;

@Configuration
public class PluginConfig {
    @Bean
    public PluginManager jarPluginManager() {
        PluginManager pluginManager = new TransFlowPluginManager();
        return pluginManager;
    }
}
