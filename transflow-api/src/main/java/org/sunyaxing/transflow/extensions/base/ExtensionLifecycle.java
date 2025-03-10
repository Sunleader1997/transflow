package org.sunyaxing.transflow.extensions.base;

import org.pf4j.ExtensionPoint;

import java.util.Properties;

public interface ExtensionLifecycle extends ExtensionPoint  {
    public void init(Properties config);

    public void destroy();
}
