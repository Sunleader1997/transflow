package org.sunyaxing.transflow;

import java.io.Serializable;


public record TransData<T>(Long offset, T data) implements Serializable {
}
