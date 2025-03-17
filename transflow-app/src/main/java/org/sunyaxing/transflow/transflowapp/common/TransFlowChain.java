package org.sunyaxing.transflow.transflowapp.common;

import lombok.Getter;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.ChainStatusEnum;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import reactor.core.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;


@Getter
public class TransFlowChain<T extends ExtensionLifecycle> implements Disposable {
    private static final Logger log = LoggerFactory.getLogger(TransFlowChain.class);
    private final String nodeId;
    private final TransFlowTypeEnum typeEnum;
    // 状态 0 未执行 1 执行中 2 执行完成
    private ChainStatusEnum status;
    private final T currentNode;
    // 属于 当前节点的哪一个 handle
    private final String handleId;
    private final List<TransFlowChain<?>> children;
    private final Map<String, Map<String, TransFlowChain<?>>> allChains;
    private final Map<String, ExtensionLifecycle> allExtensions;

    public TransFlowChain(NodeBo nodeBo, String handleId, T currentNode) {
        this.allExtensions = new ConcurrentHashMap<>();
        this.allChains = new ConcurrentHashMap<>();
        this.nodeId = nodeBo.getId();
        this.handleId = handleId;
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
        final List<TransData> res = currentNode.execDatas(handleId, orgData);
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

    public boolean addChainForSingle(String nodeId, String handleId, TransFlowChain<?> chain) {
        if (StringUtils.isNullOrEmpty(handleId)) handleId = "DEF";
        if (allChains.containsKey(nodeId)) {
            Map<String, TransFlowChain<?>> transFlowChainMap = allChains.get(nodeId);
            if (transFlowChainMap.containsKey(handleId)) {
                return false;
            } else {
                transFlowChainMap.put(handleId, chain);
                return true;
            }
        } else {
            Map<String, TransFlowChain<?>> transFlowChainMap = new ConcurrentHashMap<>();
            transFlowChainMap.put(handleId, chain);
            allChains.put(nodeId, transFlowChainMap);
            return true;
        }
    }

    public TransFlowChain<?> getChainByNodeId(String id, String targetHandle) {
        if (StringUtils.isNullOrEmpty(targetHandle)) targetHandle = "DEF";
        if (allChains.containsKey(id)) {
            Map<String, TransFlowChain<?>> transFlowChainMap = allChains.get(id);
            return transFlowChainMap.getOrDefault(targetHandle, null);
        } else {
            return null;
        }
    }

    public Map<String, TransFlowChain<?>> getChainByNodeId(String id) {
        return allChains.getOrDefault(id, null);
    }

    public void addAllExtension(String id, ExtensionLifecycle extension) {
        allExtensions.put(id, extension);
    }

    public ExtensionLifecycle getExtension(String id) {
        return allExtensions.getOrDefault(id, null);
    }

    @Override
    public void dispose() {
        currentNode.destroy();
        this.children.forEach(Disposable::dispose);
    }
}
