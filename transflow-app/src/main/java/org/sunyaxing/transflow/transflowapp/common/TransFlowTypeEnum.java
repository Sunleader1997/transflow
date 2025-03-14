package org.sunyaxing.transflow.transflowapp.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransFlowTypeEnum {
    INPUT("input"),
    FILTER("filter"),
    OUTPUT("output")
    ;
    private final String value;
}
