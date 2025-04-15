package org.sunyaxing.transflow.transflowapp.config;

import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.sunyaxing.transflow.transflowapp.config.porperties.RouteConfig;
import org.sunyaxing.transflow.transflowapp.factory.TransFlowExtensionFactory;
import org.sunyaxing.transflow.transflowapp.factory.TransFlowPluginManager;

@Configuration
@EnableConfigurationProperties({RouteConfig.class})
public class PluginConfig {
    private static final Logger log = LoggerFactory.getLogger(PluginConfig.class);

    @Bean(name = "transFlowPluginManager")
    public PluginManager transFlowPluginManager() {
        return new TransFlowPluginManager(new TransFlowExtensionFactory());
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
