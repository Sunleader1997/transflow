package org.sunyaxing.transflow.transflowapp.services;

import cn.hutool.core.collection.CollectionUtil;
import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sunyaxing.transflow.extensions.base.types.TransFlowInput;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import org.sunyaxing.transflow.transflowapp.common.ChainManager;
import org.sunyaxing.transflow.transflowapp.common.TransFlowChain;
import org.sunyaxing.transflow.transflowapp.reactor.TransFlowRunnable;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeLinkBo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TransFlowChainService {

    @Autowired
    private NodeService nodeService;
    @Autowired
    private NodeLinkService nodeLinkService;
    @Autowired
    private PluginManager pluginManager;

    public static final ConcurrentHashMap<String, List<TransFlowRunnable>> JOB_CHAINS = new ConcurrentHashMap<>();

    public void run(String jobId) {
        boolean notClean = JOB_CHAINS.containsKey(jobId);
        // 所有节点全部准备好了之后
        List<TransFlowChain<TransFlowInput>> rootChains = buildChain(jobId);
        if (notClean) throw new RuntimeException("jobId: " + jobId + " is running");
        JOB_CHAINS.put(jobId, new ArrayList<>());
        // 创建input dequeue线程开始消费数据
        rootChains.forEach(rootChain -> {
            TransFlowRunnable runnable = new TransFlowRunnable(rootChain);
            JOB_CHAINS.get(jobId).add(runnable);
        });
    }

    public void stop(String jobId) {
        List<TransFlowRunnable> runnables = JOB_CHAINS.get(jobId);
        if (CollectionUtil.isNotEmpty(runnables)) {
            runnables.forEach(TransFlowRunnable::dispose);
        }
        JOB_CHAINS.remove(jobId);
    }

    public List<TransFlowChain<TransFlowInput>> buildChain(String jobId) {
        // 初始化所有的插件
        List<NodeBo> nodeBos = nodeService.list(jobId);
        return nodeBos.stream().map(nodeBo -> {
            ExtensionLifecycle extension = pluginManager.getExtensions(ExtensionLifecycle.class, nodeBo.getPluginId()).get(0);
            // 初始化 插件
            extension.init(nodeBo.getConfig(),nodeBo.getHandles());
            // 下一个节点的连线
            List<NodeLinkBo> links = nodeLinkService.findLinksBySource(nodeBo.getId());
            // 创建 责任链
            TransFlowChain<?> chain = new TransFlowChain<>(nodeBo, extension, links);
            // 放到全局缓存
            ChainManager.addChainCache(chain);
            return chain;
        }).map(TransFlowChain::getIfIsInput).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
