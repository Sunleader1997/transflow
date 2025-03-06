package org.sunyaxing.transflow;

import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFactory;
import org.sunyaxing.transflow.factory.TransFlowExtensionFactory;

public class TransFlowPluginManager extends DefaultPluginManager {
    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new TransFlowExtensionFactory();
    }
}
