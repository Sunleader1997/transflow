package org.sunyaxing.transflow.pluginjsonfilter;

import org.pf4j.Extension;
import org.sunyaxing.transflow.extensions.TransFlowFilter;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

@Extension
public class JsonFilterExt extends TransFlowFilter {
    public JsonFilterExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public Object doFilter(Object input) {
        return input;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }
}
