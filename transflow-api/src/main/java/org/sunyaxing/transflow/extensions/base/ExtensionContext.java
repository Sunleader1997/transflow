package org.sunyaxing.transflow.extensions.base;

import org.sunyaxing.transflow.TransData;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

public record ExtensionContext<T>(BlockingDeque<TransData<T>> inputQueue) {

    public TransData<T> dequeue(){
        try {
            return inputQueue.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
