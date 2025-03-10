package org.sunyaxing.transflow.extensions.base;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

public record ExtensionContext(BlockingDeque<String> inputQueue) {

    public String dequeue(){
        try {
            return inputQueue.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
