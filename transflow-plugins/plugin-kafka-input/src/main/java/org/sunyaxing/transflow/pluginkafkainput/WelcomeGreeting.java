package org.sunyaxing.transflow.pluginkafkainput;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.extensions.ExtensionContext;
import org.sunyaxing.transflow.extensions.TransFlowInput;


@Extension
public class WelcomeGreeting implements TransFlowInput {

    private static final Logger log = LoggerFactory.getLogger(WelcomeGreeting.class);

    public WelcomeGreeting(ExtensionContext extensionContext) {
        log.info("create");
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public String dequeue() {
        return "";
    }
}