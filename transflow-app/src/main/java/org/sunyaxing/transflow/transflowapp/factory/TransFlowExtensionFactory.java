package org.sunyaxing.transflow.transflowapp.factory;

import org.pf4j.ExtensionFactory;
import org.pf4j.PluginRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;

public class TransFlowExtensionFactory implements ExtensionFactory {
    private static final Logger log = LoggerFactory.getLogger(TransFlowExtensionFactory.class);

//    protected ExtensionContext extensionContext;
//
//    public TransFlowExtensionFactory(ExtensionContext extensionContext) {
//        this.extensionContext = extensionContext;
//    }

    public TransFlowExtensionFactory() {
    }

    @Override
    public <T> T create(Class<T> extensionClass) {
        try {
            Constructor<T> constructor = extensionClass.getConstructor(ExtensionContext.class);
            return constructor.newInstance(new ExtensionContext(new LinkedBlockingDeque<>(1000)));
//            return constructor.newInstance(this.extensionContext);
        } catch (Exception e) {
            throw new PluginRuntimeException(e);
        }
    }
}
