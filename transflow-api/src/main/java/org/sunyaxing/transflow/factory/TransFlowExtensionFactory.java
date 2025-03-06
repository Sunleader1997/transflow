package org.sunyaxing.transflow.factory;

import org.pf4j.ExtensionFactory;
import org.pf4j.PluginRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.extensions.ExtensionContext;

import java.lang.reflect.Constructor;

public class TransFlowExtensionFactory implements ExtensionFactory {
    private static final Logger log = LoggerFactory.getLogger(TransFlowExtensionFactory.class);

    @Override
    public <T> T create(Class<T> extensionClass) {
        try {
            ExtensionContext extensionContext = new ExtensionContext();
            Constructor<T> constructor = extensionClass.getConstructor(ExtensionContext.class);
            return constructor.newInstance(extensionContext);
        } catch (Exception e) {
            throw new PluginRuntimeException(e);
        }
    }
}
