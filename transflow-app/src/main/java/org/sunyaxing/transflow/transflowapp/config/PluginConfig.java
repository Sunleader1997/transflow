package org.sunyaxing.transflow.transflowapp.config;

import org.pf4j.JarPluginManager;
import org.pf4j.PluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PluginConfig {
    @Bean
    public PluginManager jarPluginManager() {
        return new JarPluginManager();
    }
}
