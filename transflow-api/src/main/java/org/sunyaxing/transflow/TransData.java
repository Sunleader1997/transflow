package org.sunyaxing.transflow;

import java.io.Serializable;


public record TransData(Long offset, Object data) implements Serializable {
    public <T> T getData(Class<T> clazz) {
        if (String.class.equals(clazz)) {
            return (T) data.toString();
        }
        return clazz.cast(data);
    }
    public boolean isType(Class<?> tClass){
        return tClass.isInstance(data);
    }
}
