package org.sunyaxing.transflow.pluginkafkainput;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.TransFlowInput;


@Extension
public class WelcomeGreeting extends TransFlowInput {

    private static final Logger log = LoggerFactory.getLogger(WelcomeGreeting.class);
    private Thread kafkaThread ;

    public WelcomeGreeting(ExtensionContext extensionContext) {
        super(extensionContext);
        log.info("create");
    }

    @Override
    public void init() {
        setMaxBatchSize(10);
        kafkaThread = Thread.ofVirtual().start(() -> {
            while (!Thread.currentThread().isInterrupted()){
                try{
                    this.inputQueue.put("Welcome to use KafkaInputPlugin");
                    log.info("added");
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    @Override
    public void destroy() {
        kafkaThread.interrupt();
    }

    @Override
    public String dequeue() {
        return this.inputQueue.poll();
    }
}