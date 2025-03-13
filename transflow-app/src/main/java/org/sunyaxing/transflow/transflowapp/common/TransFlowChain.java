package org.sunyaxing.transflow.transflowapp.common;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.ChainStatusEnum;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


@Getter
public class TransFlowChain<T extends ExtensionLifecycle> {
    private static final Logger log = LoggerFactory.getLogger(TransFlowChain.class);
    private final Long nodeId;
    private final TransFlowTypeEnum typeEnum;
    // 状态 0 未执行 1 执行中 2 执行完成
    private ChainStatusEnum status;
    private final T currentNode;
    private final List<TransFlowChain<?>> children;

    public TransFlowChain(NodeBo nodeBo, T currentNode) {
        this.nodeId = nodeBo.getId();
        this.typeEnum = nodeBo.getNodeType();
        this.status = ChainStatusEnum.INIT;
        this.currentNode = currentNode;
        this.children = new ArrayList<>();
    }

    public void addChild(TransFlowChain<?> child) {
        children.add(child);
    }

    public void exec(List<TransData> orgData) {
        sign(ChainStatusEnum.RUNNING);
        final List<TransData> res = currentNode.execDatas(orgData);
        if (!children.isEmpty()) {
            CountDownLatch countDownLatch = new CountDownLatch(children.size());
            children.forEach(child -> {
                Thread.ofVirtual().start(() -> {
                    try {
                        child.exec(res);
                        child.sign(ChainStatusEnum.FINISHED);
                    } catch (Exception e) {
                        log.error("子节点执行异常", e);
                        child.sign(ChainStatusEnum.ERROR);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                sign(ChainStatusEnum.ERROR);
            }
        }
    }

    public void sign(ChainStatusEnum status) {
        this.status = status;
    }
}
