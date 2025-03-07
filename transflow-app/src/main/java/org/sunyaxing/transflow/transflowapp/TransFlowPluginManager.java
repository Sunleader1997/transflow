package org.sunyaxing.transflow.transflowapp;

import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFactory;
import org.sunyaxing.transflow.transflowapp.factory.TransFlowExtensionFactory;

public class TransFlowPluginManager extends DefaultPluginManager {

    private final TransFlowExtensionFactory transFlowExtensionFactory;

    public TransFlowPluginManager(TransFlowExtensionFactory transFlowExtensionFactory) {
        this.transFlowExtensionFactory = transFlowExtensionFactory;
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return transFlowExtensionFactory;
    }
}
