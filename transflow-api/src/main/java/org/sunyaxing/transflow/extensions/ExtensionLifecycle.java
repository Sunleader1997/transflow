package org.sunyaxing.transflow.extensions;

import org.pf4j.ExtensionPoint;

public interface ExtensionLifecycle extends ExtensionPoint  {
    public void init();

    public void destroy();
}
