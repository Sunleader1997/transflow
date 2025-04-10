package org.sunyaxing.transflow.extensions.handlers;


import org.sunyaxing.transflow.TransData;

public class DefaultStringHandler implements TransDataHandler<String> {
    private final String data;

    public DefaultStringHandler(String data) {
        this.data = data;
    }

    @Override
    public String resolve(TransData transData) {
        return data;
    }
}
