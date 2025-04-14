package org.sunyaxing.transflow.extensions.base;

import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.types.TransFlowInput;

public class InputUtil<FT> {
    private final TransFlowInput<FT, ?> input;

    public InputUtil(TransFlowInput<FT, ?> input) {
        this.input = input;
    }

    public void handle(String handleValue, TransData<FT> data) {
        input.handleByValue(handleValue, data);
    }
}
