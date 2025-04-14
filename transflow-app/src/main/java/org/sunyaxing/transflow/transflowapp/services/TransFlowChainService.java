package org.sunyaxing.transflow.transflowapp.services;

import cn.hutool.core.collection.CollectionUtil;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import org.sunyaxing.transflow.extensions.base.types.TransFlowInput;
import org.sunyaxing.transflow.transflowapp.common.ChainManager;
import org.sunyaxing.transflow.transflowapp.common.TransFlowChain;
import org.sunyaxing.transflow.transflowapp.entity.JobEntity;
import org.sunyaxing.transflow.transflowapp.reactor.TransFlowRunnable;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeLinkBo;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class TransFlowChainService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TransFlowChainService.class);
    @Autowired
    private NodeService nodeService;
    @Autowired
    private NodeLinkService nodeLinkService;
    @Autowired
    private PluginManager pluginManager;
    @Autowired
    private JobService jobService;
    private final ReentrantLock lock = new ReentrantLock();
    /**
     * 一个 job 内部有多个 input
     */
    public static final ConcurrentHashMap<String, List<TransFlowRunnable>> JOB_CHAINS = new ConcurrentHashMap<>();

    public void safeRune(String jobId) {
        try {
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                run(jobId);
            } else {
                throw new RuntimeException("someone is starting, please wait");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void run(String jobId) {
        // 执行之前，先判断是否上一个任务清理结束
        boolean notClean = JOB_CHAINS.containsKey(jobId);
        if (notClean) throw new RuntimeException("jobId: " + jobId + " is running");
        // 重新构建所有节点
        List<TransFlowChain<TransFlowInput>> rootChains = buildChain(jobId);
        // 创建input dequeue线程开始消费数据
        List<TransFlowRunnable> runnables = rootChains.stream().map(rootChain -> {
            TransFlowRunnable runnable = new TransFlowRunnable(rootChain);
            runnable.run();
            return runnable;
        }).collect(Collectors.toList());
        JOB_CHAINS.put(jobId, runnables);
    }

    /**
     * 仅关闭 input 线程
     *
     * @param jobId
     */
    public void stop(String jobId, boolean safe) {
        List<TransFlowRunnable> runnables = JOB_CHAINS.get(jobId);
        if (CollectionUtil.isNotEmpty(runnables)) {
            runnables.forEach(runnable -> {
                runnable.dispose(safe);
            });
        }
        JOB_CHAINS.remove(jobId);
    }

    /**
     * 构建 责任链
     */
    public List<TransFlowChain<TransFlowInput>> buildChain(String jobId) {
        // 初始化所有的插件
        List<NodeBo> nodeBos = nodeService.list(jobId);
        return nodeBos.stream().map(nodeBo -> {
            ExtensionLifecycle extension = pluginManager.getExtensions(ExtensionLifecycle.class, nodeBo.getPluginId()).get(0);
            // 初始化 插件
            extension.init(nodeBo.getConfig(), nodeBo.getHandles());
            // 下一个节点的连线
            List<NodeLinkBo> links = nodeLinkService.findLinksBySource(nodeBo.getId());
            // 创建 责任链
            TransFlowChain<?> chain = new TransFlowChain<>(nodeBo, extension, links);
            // 放到全局缓存
            ChainManager.addChainCache(chain);
            return chain;
        }).map(TransFlowChain::getIfIsInput).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void run(ApplicationArguments args) {
        jobService.lambdaQuery()
                .eq(JobEntity::getRestart, true)
                .list()
                .forEach(jobEntity -> {
                    String jobId = jobEntity.getId();
                    try {
                        log.info("job 自启 {}", jobId);
                        run(jobId);
                    } catch (Exception e) {
                        log.error("job 执行失败", e);
                    }
                });
    }
}
