package org.sunyaxing.transflow.extensions.handlers;

import org.sunyaxing.transflow.TransData;

public abstract class AbstractMiddleHandler implements Handler<TransData,Boolean> {
    private String key;
    private String value;
}
