package org.sunyaxing.transflow.common;

import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class TransFlowChain {
    private final ExtensionLifecycle currentNode;
    private final List<TransFlowChain> children;

    public TransFlowChain(ExtensionLifecycle currentNode) {
        this.currentNode = currentNode;
        this.children = new ArrayList<>();
    }

    public void addChild(TransFlowChain child) {
        children.add(child);
    }

    public void exec(List<TransData> orgData) {
        final List<TransData> res = currentNode.execDatas(orgData);
        if (!children.isEmpty()) {
            CountDownLatch countDownLatch = new CountDownLatch(children.size());
            children.forEach(child -> {
                Thread.ofVirtual().start(() -> {
                    try {
                        child.exec(res);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public ExtensionLifecycle getCurrentNode() {
        return currentNode;
    }

    public List<TransFlowChain> getChildren() {
        return children;
    }
}
