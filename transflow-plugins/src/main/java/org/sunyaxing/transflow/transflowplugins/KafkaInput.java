package org.sunyaxing.transflow.transflowplugins;

import org.pf4j.Extension;
import org.sunyaxing.transflow.extensions.TransFlowInput;

@Extension
public class KafkaInput implements TransFlowInput {
    @Override
    public String dequeue() {
        return "";
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }
}
