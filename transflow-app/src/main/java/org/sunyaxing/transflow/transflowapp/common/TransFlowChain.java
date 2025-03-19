package org.sunyaxing.transflow.transflowapp.common;

import lombok.Getter;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeLinkBo;
import reactor.core.Disposable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

/**
 * 每一个 chain 需要维护一个队列
 * 每个 chain 有单独的线程，负责拉取消息并消费
 *
 * @param <T>
 */
@Getter
public class TransFlowChain<T extends ExtensionLifecycle> implements Disposable, Runnable {
    private static final Logger log = LoggerFactory.getLogger(TransFlowChain.class);
    private final NodeBo nodeBo;
    private final String nodeId;
    private final TransFlowTypeEnum typeEnum;
    private final T currentNode;
    // 下一个节点的连线
    private final List<NodeLinkBo> linkBos;

    private final BlockingDeque<HandleData> queue;


    public TransFlowChain(NodeBo nodeBo, T currentNode, List<NodeLinkBo> links) {
        this.nodeBo = nodeBo;
        this.linkBos = links;
        this.nodeId = nodeBo.getId();
        this.typeEnum = nodeBo.getNodeType();
        this.currentNode = currentNode;

        this.queue = new LinkedBlockingDeque<>(1000);
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


    /**
     * 根据产生的HandleData，选择对应连线
     *
     * @param handleData
     */
    public List<NodeLinkBo> selectOutHandle(HandleData handleData) {
        return this.linkBos.stream().filter(linkBo -> {
            if (StringUtils.isNullOrEmpty(handleData.getHandleId())) { // 如果源数据没有对应 handle,直接输出
                return true;
            } else {
                return handleData.getHandleId().equals(linkBo.getSourceHandle()); // 过滤元数据对应连线
            }
        }).collect(Collectors.toList());
    }

    /**
     * 根据连线下发到下一个节点对应 handle
     */
    public void sendDataToNextHandle(HandleData handleData, List<NodeLinkBo> links) {
        links.forEach(linkBo -> {
            final String nextNodeId = linkBo.getTargetId();
            final String nextHandleId = linkBo.getTargetHandle();
            if (ChainManager.containsChain(nextNodeId)) {
                TransFlowChain<?> nextChain = ChainManager.getChainCache(nextNodeId);
                HandleData nextHandleData = new HandleData(nextHandleId, handleData.getTransData());
                nextChain.addQueue(nextHandleData);
            }
        });
    }

    public void addQueue(HandleData nextHandleData) {
        try {
            // 如果队列满了，调用该方法的线程会阻塞
            this.queue.put(nextHandleData);
        } catch (InterruptedException e) {
            log.error("队列异常", e);
        }
    }

    /**
     * 仅关闭 input 线程
     * 等待子节点把剩余数据处理完
     */
    @Override
    public void dispose() {
        // 结束当前线程
        Thread.currentThread().interrupt();
        // 销毁节点的监听器
        this.currentNode.destroy();
    }

    @Override
    public void run() {
        HandleData handleData = this.queue.poll();
        if (handleData != null) {
            // 本节点先处理数据
            List<HandleData> results = this.currentNode.exec(handleData);
            results.forEach(result -> {
                // 选择该数据对应的下一个连线
                List<NodeLinkBo> links = this.selectOutHandle(result);
                // 将该数据发送到下一个节点
                this.sendDataToNextHandle(result, links);
            });
        }
    }
}
