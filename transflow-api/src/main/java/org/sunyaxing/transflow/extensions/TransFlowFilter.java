package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

import java.util.List;

public abstract class TransFlowFilter implements ExtensionLifecycle {

    protected TransFlowFilter nextFilter;
    protected ExtensionContext extensionContext;

    public TransFlowFilter(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }

    public abstract List<TransData> doFilter(List<TransData> input);

    public void addNext(TransFlowFilter nextFilter) {
        if (this.nextFilter != null) {
            this.nextFilter.addNext(nextFilter);
        } else {
            this.nextFilter = nextFilter;
        }
    }

    public List<TransData> exec(List<TransData> orgData) {
        List<TransData> result = doFilter(orgData);
        if (this.nextFilter == null) {
            return result;
        }
        return this.nextFilter.exec(result);
    }
}
