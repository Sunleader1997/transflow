package org.sunyaxing.transflow.transflowapp.services;

import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import org.sunyaxing.transflow.transflowapp.common.TransFlowChain;
import org.sunyaxing.transflow.transflowapp.reactor.TransFlowRunnable;
import org.sunyaxing.transflow.transflowapp.services.bos.JobBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeLinkBo;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransFlowChainService {

    @Autowired
    private JobService jobService;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private NodeLinkService nodeLinkService;
    @Autowired
    private PluginManager pluginManager;

    public static final ConcurrentHashMap<Long, TransFlowRunnable> JOB_CHAINS = new ConcurrentHashMap<>();

    public void run(Long jobId) {
        boolean isRunning = JOB_CHAINS.containsKey(jobId);
        if (isRunning) {
            throw new RuntimeException("job is running");
        } else {
            TransFlowChain<TransFlowInput> chain = buildChain(jobId);
            TransFlowRunnable runnable = new TransFlowRunnable(chain);
            runnable.run();
            JOB_CHAINS.put(jobId, runnable);
        }
    }

    public void stop(Long jobId) {
        TransFlowRunnable chain = JOB_CHAINS.get(jobId);
        if (chain != null) {
            chain.dispose();
            JOB_CHAINS.remove(jobId);
        }
    }

    public TransFlowRunnable get(Long jobId) {
        return JOB_CHAINS.get(jobId);
    }

    public boolean hasKey(Long jobId) {
        return JOB_CHAINS.containsKey(jobId);
    }

    /**
     * 构建全链路 包括头部 input 节点
     *
     * @param jobId
     * @return
     */
    public TransFlowChain<TransFlowInput> buildChain(Long jobId) {
        JobBo jobBo = jobService.boById(jobId);
        // 获取输入节点
        Long inputId = jobBo.getInputId();
        NodeBo inputNode = nodeService.boById(inputId);
        // 获取 input 插件
        TransFlowInput transFlowInput = pluginManager.getExtensions(TransFlowInput.class, inputNode.getPluginId()).getFirst();
        // 执行 input 节点初始化
        transFlowInput.init(inputNode.getConfig());
        // 创建 责任链
        TransFlowChain<TransFlowInput> startChain = new TransFlowChain<>(inputNode, transFlowInput);
        buildChain(startChain);
        return startChain;
    }

    // 递归创建责任链
    public void buildChain(TransFlowChain<?> parentChain) {
        Long sourceId = parentChain.getNodeId();
        // 以当前节点为源的连线
        List<NodeLinkBo> links = nodeLinkService.findLinksBySource(sourceId);
        // 下一个节点数据
        List<NodeBo> nodeBos = links.stream().map(link -> {
            return nodeService.boById(link.getTargetId());
        }).filter(Objects::nonNull).toList();
        nodeBos.forEach(nodeBo -> {
            ExtensionLifecycle extension = pluginManager.getExtensions(ExtensionLifecycle.class, nodeBo.getPluginId()).getFirst();
            // 初始化 插件
            extension.init(nodeBo.getConfig());
            // 创建 子责任链
            TransFlowChain<?> chain = new TransFlowChain<>(nodeBo, extension);
            // 添加到父链
            parentChain.addChild(chain);
            // 递归创建子责任链
            buildChain(chain);
        });
    }
}
