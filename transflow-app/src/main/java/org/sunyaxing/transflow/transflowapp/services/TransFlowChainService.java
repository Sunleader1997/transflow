package org.sunyaxing.transflow.transflowapp.services;

import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import org.sunyaxing.transflow.transflowapp.common.TransFlowChain;
import org.sunyaxing.transflow.transflowapp.reactor.TransFlowRunnable;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeLinkBo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransFlowChainService {

    @Autowired
    private NodeService nodeService;
    @Autowired
    private NodeLinkService nodeLinkService;
    @Autowired
    private PluginManager pluginManager;

    public static final ConcurrentHashMap<String, TransFlowRunnable> JOB_CHAINS = new ConcurrentHashMap<>();

    public void run(String jobId) {
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

    public void stop(String jobId) {
        TransFlowRunnable chain = JOB_CHAINS.get(jobId);
        if (chain != null) {
            chain.dispose();
            JOB_CHAINS.remove(jobId);
        }
    }

    public TransFlowRunnable get(String jobId) {
        return JOB_CHAINS.get(jobId);
    }

    public boolean hasKey(String jobId) {
        return JOB_CHAINS.containsKey(jobId);
    }

    /**
     * 构建全链路 包括头部 input 节点
     *
     * @param jobId
     * @return
     */
    public TransFlowChain<TransFlowInput> buildChain(String jobId) {
        // JobBo jobBo = jobService.boById(jobId);
        // 获取输入节点
        NodeBo inputNode = nodeService.boByJobId(jobId);
        // 获取 input 插件
        TransFlowInput transFlowInput = pluginManager.getExtensions(TransFlowInput.class, inputNode.getPluginId()).getFirst();
        // 执行 input 节点初始化
        transFlowInput.init(inputNode.getConfig());
        // 创建 责任链
        TransFlowChain<TransFlowInput> startChain = new TransFlowChain<>(inputNode, null, transFlowInput);
        startChain.addAllExtension(inputNode.getId(), transFlowInput);
        try {
            buildChain(startChain, startChain);
        } catch (Exception e) {
            startChain.dispose();
            throw new RuntimeException("构建责任链失败");
        }
        return startChain;
    }

    // 递归创建责任链
    public void buildChain(TransFlowChain<?> rootChain, TransFlowChain<?> parentChain) {
        String sourceId = parentChain.getNodeId();
        // 以当前节点为源的连线
        List<NodeLinkBo> links = nodeLinkService.findLinksBySource(sourceId);
        // 下一个节点数据
        links.forEach(link -> {
            NodeBo nodeBo = nodeService.boById(link.getTargetId());
            if (Objects.nonNull(nodeBo)) {
                TransFlowChain<?> chain = rootChain.getChainByNodeId(nodeBo.getId(), link.getTargetHandle());
                // 如果没有缓存，就创建 chain 并添加到根节点缓存
                if (Objects.isNull(chain)) { // 如果没有创建过 chain，则新建并初始化
                    ExtensionLifecycle extension = rootChain.getExtension(nodeBo.getId());
                    if (Objects.isNull(extension)) {
                        // extension 在初始化之后其实是可以再次利用的，防止资源重复创建
                        extension = pluginManager.getExtensions(ExtensionLifecycle.class, nodeBo.getPluginId()).getFirst();
                        // 初始化 插件
                        extension.init(nodeBo.getConfig());
                        // 添加到缓存
                        rootChain.addAllExtension(nodeBo.getId(), extension);
                    }
                    Optional<Handle> linkHandle = nodeBo.getHandles().stream()
                            .filter(handle -> handle.getId().equals(link.getTargetHandle())).findFirst();
                    // 创建 责任链
                    chain = new TransFlowChain<>(nodeBo, linkHandle.map(Handle::getValue).orElse(null), extension);
                    // 添加到根节点缓存
                    rootChain.addChainForSingle(nodeBo.getId(), link.getTargetHandle(), chain);
                    // 递归创建子责任链，下次再遇到本节点时，将会命中缓存
                    buildChain(rootChain, chain);
                }
                // 添加到父链
                parentChain.addChild(chain);
            }
        });
    }
}
