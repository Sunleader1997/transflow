package org.sunyaxing.transflow;

import java.io.Serializable;


public record TransData(Long offset, Object data) implements Serializable {
    public <T> T getData(Class<T> clazz) {
        return clazz.cast(data);
    }
}
