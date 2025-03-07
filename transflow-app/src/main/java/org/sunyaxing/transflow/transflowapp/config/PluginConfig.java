package org.sunyaxing.transflow.transflowapp.config;

import org.pf4j.PluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.transflowapp.TransFlowPluginManager;
import org.sunyaxing.transflow.transflowapp.factory.TransFlowExtensionFactory;

import java.util.concurrent.LinkedBlockingDeque;

@Configuration
public class PluginConfig {
    @Bean
    public ExtensionContext extensionContext() {
        return new ExtensionContext(new LinkedBlockingDeque<>(1000));
    }

    @Bean
    public TransFlowExtensionFactory transFlowExtensionFactory(ExtensionContext extensionContext) {
        return new TransFlowExtensionFactory(extensionContext);
    }

    @Bean
    public PluginManager transFlowPluginManager(TransFlowExtensionFactory transFlowExtensionFactory) {
        return new TransFlowPluginManager(transFlowExtensionFactory);
    }
}
