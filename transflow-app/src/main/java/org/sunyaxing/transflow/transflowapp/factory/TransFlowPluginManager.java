package org.sunyaxing.transflow.transflowapp.factory;

import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFactory;

public class TransFlowPluginManager extends DefaultPluginManager {

    private final TransFlowExtensionFactory transFlowExtensionFactory;

    public TransFlowPluginManager(TransFlowExtensionFactory transFlowExtensionFactory) {
        this.transFlowExtensionFactory = transFlowExtensionFactory;
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return transFlowExtensionFactory;
    }

    @Override
    public ExtensionFactory getExtensionFactory() {
        return this.transFlowExtensionFactory;
    }
}
