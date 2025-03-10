package org.sunyaxing.transflow.extensions;

import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

public abstract class TransFlowFilter<T, R> implements ExtensionLifecycle {

    protected TransFlowFilter<R, ?> nextFilter;
    protected ExtensionContext extensionContext;

    public TransFlowFilter(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }

    public abstract TransData<R> doFilter(TransData<T> input);

    public void addNext(TransFlowFilter nextFilter) {
        if (this.nextFilter != null) {
            this.nextFilter.addNext(nextFilter);
        } else {
            this.nextFilter = nextFilter;
        }
    }

    public TransData<?> exec(TransData<T> orgData) {
        TransData<R> result = doFilter(orgData);
        if (this.nextFilter == null) {
            return result;
        }
        return this.nextFilter.exec(result);
    }
}
