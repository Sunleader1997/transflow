package org.sunyaxing.transflow.transflowapp.common;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeLinkBo;
import reactor.core.Disposable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;


@Getter
public class TransFlowChain<T extends ExtensionLifecycle> implements Disposable {
    private static final Logger log = LoggerFactory.getLogger(TransFlowChain.class);
    private final NodeBo nodeBo;
    private final String nodeId;
    private final TransFlowTypeEnum typeEnum;
    private final T currentNode;
    // 下一个节点的连线
    private final List<NodeLinkBo> linkBos;
    // 单例静态缓存
    private final static Map<String, TransFlowChain<?>> CHAIN_CACHE = new ConcurrentHashMap<>();

    public TransFlowChain(NodeBo nodeBo, T currentNode, List<NodeLinkBo> links) {
        this.nodeBo = nodeBo;
        this.linkBos = links;
        this.nodeId = nodeBo.getId();
        this.typeEnum = nodeBo.getNodeType();
        this.currentNode = currentNode;
    }

    public boolean isInput() {
        return currentNode instanceof TransFlowInput;
    }

    public TransFlowChain<TransFlowInput> getIfIsInput() {
        if (isInput()) {
            return (TransFlowChain<TransFlowInput>) this;
        } else {
            return null;
        }
    }

    public static void addChainCache(TransFlowChain<?> chain) {
        if (CHAIN_CACHE.containsKey(chain.getNodeId())) {
            // 销毁线程
            CHAIN_CACHE.get(chain.getNodeId()).dispose();
            // 移除引用
            CHAIN_CACHE.remove(chain.getNodeId());
        }
        // 重新放到缓存
        CHAIN_CACHE.put(chain.getNodeId(), chain);
    }

    public static TransFlowChain<?> getChainCache(String nodeId) {
        return CHAIN_CACHE.get(nodeId);
    }

    public void exec(String handleValue, List<TransData> orgData) {
        final List<TransData> res = currentNode.execDatas(handleValue, orgData);
        execForChild(res);
    }

    public void execForChild(List<TransData> orgData) {
        if (!linkBos.isEmpty()) {
            CountDownLatch countDownLatch = new CountDownLatch(linkBos.size());
            // 通过下一个节点的连线，查询出实际 chain, 执行其chain的handle
            linkBos.forEach(linkBo -> {
                final String nextNodeId = linkBo.getTargetId();
                final String nextHandle = linkBo.getTargetHandle();
                if (CHAIN_CACHE.containsKey(nextNodeId)) {
                    TransFlowChain<?> nextChain = CHAIN_CACHE.get(nextNodeId);
                    Thread.ofVirtual().start(() -> {
                        try {
                            String handleValue = nextChain.getNodeBo().getHandle(nextHandle);
                            nextChain.exec(handleValue, orgData);
                        } catch (Exception e) {
                            log.error("子节点执行异常", e);
                        } finally {
                            countDownLatch.countDown();
                        }
                    });
                } else {
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dispose() {
        try {
            log.info("--------------销毁 {}----------------", this.nodeBo.getName());
            currentNode.destroy();
            CHAIN_CACHE.remove(this.nodeId);
        } catch (Exception e) {
            log.error("销毁节点异常", e);
        }
        this.linkBos.forEach(linkBo -> {
            String nextNodeId = linkBo.getTargetId();
            if (CHAIN_CACHE.containsKey(nextNodeId)) {
                try {
                    CHAIN_CACHE.get(nextNodeId).dispose();
                } catch (Exception e) {
                    log.error("销毁子节点异常", e);
                }
            }
        });
    }
}
