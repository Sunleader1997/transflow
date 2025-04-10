package org.sunyaxing.transflow.extensions.handlers;

public interface Handler<T, R> {
    R resolve(T data);
}
