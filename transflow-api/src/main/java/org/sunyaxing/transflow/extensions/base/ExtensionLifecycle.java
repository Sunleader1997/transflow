package org.sunyaxing.transflow.extensions.base;

import org.pf4j.ExtensionPoint;
import org.sunyaxing.transflow.TransData;

import java.util.List;
import java.util.Properties;

public interface ExtensionLifecycle extends ExtensionPoint  {
    public void init(Properties config);
    public abstract List<TransData> execDatas(List<TransData> data);
    public void destroy();
}
