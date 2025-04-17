package org.sunyaxing.transflow.transflowapp.services;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import org.sunyaxing.transflow.extensions.base.types.TransFlowInput;
import org.sunyaxing.transflow.transflowapp.common.ChainManager;
import org.sunyaxing.transflow.transflowapp.common.TransFlowChain;
import org.sunyaxing.transflow.transflowapp.common.TransFlowTypeEnum;
import org.sunyaxing.transflow.transflowapp.config.porperties.Route;
import org.sunyaxing.transflow.transflowapp.config.porperties.RouteConfig;
import org.sunyaxing.transflow.transflowapp.entity.JobEntity;
import org.sunyaxing.transflow.transflowapp.entity.NodeEntity;
import org.sunyaxing.transflow.transflowapp.entity.NodeLinkEntity;
import org.sunyaxing.transflow.transflowapp.reactor.TransFlowRunnable;
import org.sunyaxing.transflow.transflowapp.services.bos.JobBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeLinkBo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
        try {
            initFromYml(args);
        } catch (Exception e) {
            log.error("job 初始化失败", e);
        }
    }

    @Autowired
    private RouteConfig routeConfig;

    public void initFromYml(ApplicationArguments args) {
        // 根据yml文件初始化
        if (routeConfig.getEnable()==null || !routeConfig.getEnable()) return;
        log.info("数据清理");
        AtomicInteger countInput = new AtomicInteger();
        nodeService.lambdaQuery()
                .eq(NodeEntity::getJobId, routeConfig.getJobId())
                .list().forEach(nodeEntity -> {
                    nodeService.removeById(nodeEntity.getId());
                    QueryWrapper<NodeLinkEntity> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda()
                            .eq(NodeLinkEntity::getSourceId, nodeEntity.getId())
                            .or()
                            .eq(NodeLinkEntity::getTargetId, nodeEntity.getId());
                    nodeLinkService.remove(queryWrapper);
                });
        log.info("创建系统预置路由 PANEL");
        JobBo jobBo = new JobBo();
        jobBo.setId(routeConfig.getJobId());
        jobBo.setName("系统预置");
        jobBo.setDescription("系统预置" + DateUtil.now());
        jobBo.setRestart(false);
        jobService.save(jobBo);
        log.info("创建系统预置路由 PANEL 完成");
        Map<String, NodeBo> inputs = new HashMap();
        Map<String, NodeBo> gateways = new HashMap<>();
        Map<String, List<NodeBo>> execNodes = new HashMap<>();
        Map<String, NodeBo> outs = new HashMap();
        // 创建 INPUT 节点
        for (Route route : routeConfig.getRoutes()) {
            String srcTopic = route.getSrcTopic();
            String dsepName = ReUtil.get("(^[A-Za-z]+_[A-Za-z]+)_.*", srcTopic, 1);
            boolean created = inputs.containsKey(dsepName);
            if (created) {
                log.info("节点 INPUT {} 已创建", dsepName);
                NodeBo nodeBo = inputs.get(dsepName);
                String handleValue = nodeBo.getHandle(srcTopic);
                if (StrUtil.isEmpty(handleValue)) {
                    log.info("添加 HANDLE {}", srcTopic);
                    Handle handle = new Handle();
                    handle.setId(srcTopic);
                    handle.setValue(srcTopic);
                    nodeBo.getHandles().add(handle);
                    NodeBo nodeSaved = nodeService.save(nodeBo);
                    inputs.put(dsepName, nodeSaved);
                } else {
                    log.info("HANDLE {} 已存在，跳过处理", srcTopic);
                }
            } else {
                log.info("创建节点 INPUT {}", dsepName);
                NodeBo nodeBo = new NodeBo();
                nodeBo.setName(dsepName);
                nodeBo.setJobId(routeConfig.getJobId());
                nodeBo.setNodeType(TransFlowTypeEnum.INPUT);
                nodeBo.setPluginId("plugin-kafka-input");
                JSONObject config = new JSONObject();
                config.put("bootstrap-servers", "127.0.0.1:9093");
                config.put("group-id", "transflow");
                config.put("max-poll-records", "1000");
                nodeBo.setConfig(config);
                log.info("初始化 HANDLE {}", srcTopic);
                List<Handle> handles = new ArrayList<>();
                Handle handle = new Handle();
                handle.setId(srcTopic);
                handle.setValue(srcTopic);
                handles.add(handle);
                nodeBo.setHandles(handles);
                nodeBo.setX(countInput.getAndDecrement() * 1300);
                nodeBo.setY(0);
                log.info("INPUT 节点 {} 创建成功", dsepName);
                NodeBo nodeSaved = nodeService.save(nodeBo);
                inputs.put(dsepName, nodeSaved);
            }
            String[] dstTopics = route.getDstTopic();
            if (dstTopics != null) {
                for (String dstTopic : dstTopics) {
                    if (StrUtil.isEmpty(dstTopic)) continue;
                    String outName = ReUtil.get("(^[A-Za-z]+_[A-Za-z]+)_.*", srcTopic, 1);
                    if (outs.containsKey(outName)) {
                        log.info("已存在节点 OUTPUT {}", outName);
                        NodeBo outBo = outs.get(outName);
                        String handleValue = outBo.getHandle(dstTopic);
                        if (StrUtil.isEmpty(handleValue)) {
                            log.info("添加 HANDLE {}", dstTopic);
                            Handle handle = new Handle();
                            handle.setId(dstTopic);
                            handle.setValue(dstTopic);
                            outBo.getHandles().add(handle);
                            NodeBo outSaved = nodeService.save(outBo);
                            outs.put(outName, outSaved);
                        } else {
                            log.info("HANDLE {} 已存在，跳过处理", srcTopic);
                        }
                    } else {
                        NodeBo inputNode = inputs.get(dsepName);
                        log.info("创建节点 OUTPUT {}", outName);
                        NodeBo nodeBo = new NodeBo();
                        nodeBo.setName(outName);
                        nodeBo.setJobId(routeConfig.getJobId());
                        nodeBo.setNodeType(TransFlowTypeEnum.OUTPUT);
                        nodeBo.setPluginId("plugin-kafka-output");
                        nodeBo.setX(inputNode.getX() + 900);
                        nodeBo.setY(0);
                        JSONObject config = new JSONObject();
                        config.put("bootstrap-servers", "127.0.0.1:9093");
                        nodeBo.setConfig(config);
                        log.info("初始化 OUTPUT HANDLE");
                        List<Handle> handles = new ArrayList<>();
                        Handle handle = new Handle();
                        handle.setId(dstTopic);
                        handle.setValue(dstTopic);
                        handles.add(handle);
                        nodeBo.setHandles(handles);
                        NodeBo outSaved = nodeService.save(nodeBo);
                        outs.put(outName, outSaved);
                    }
                }
            }
            log.info("创建节点 GATEWAY {}", dsepName);
            String type = route.getType();
            switch (type) {
                case "condition":
                    StringBuilder script = new StringBuilder();
                    String condition = route.getConditionIf();
                    log.info("组装条件语句 {}", condition);
                    String execute = route.getExecute();
                    String conditionRepair = condition
                            .replaceAll("msg\\.", "data.")
                            .replaceAll("&&", "&&\n")
                            .replaceAll("\\|\\|", "||\n");
                            //.replaceAll("nil", "null")
                            //.replaceAll("\'", "\"");
                    script.append("return ").append(conditionRepair).append(";");

                    NodeBo inputNode = inputs.get(dsepName);
                    // 对同一个数据来源的 gateway 做条件合并
                    boolean exist = gateways.containsKey(srcTopic);
                    String currentHandleId = "";
                    if(exist){
                        NodeBo nodeBo = gateways.get(srcTopic);
                        currentHandleId = nodeBo.getHandles().size()+"";
                        Handle handle = new Handle();
                        handle.setId(currentHandleId);
                        handle.setValue(conditionRepair);
                        nodeBo.getHandles().add(handle);
                        nodeService.save(nodeBo);
                    }else{
                        currentHandleId = "0";
                        log.info("创建节点 GATEWAY");
                        NodeBo nodeBo = new NodeBo();
                        nodeBo.setName(srcTopic);
                        nodeBo.setJobId(routeConfig.getJobId());
                        nodeBo.setNodeType(TransFlowTypeEnum.GATEWAY);
                        nodeBo.setPluginId("plugin-json-filter");
                        nodeBo.setX(inputNode.getX() + 400);
                        nodeBo.setY(0);
                        JSONObject config = new JSONObject();
                        nodeBo.setConfig(config);
                        log.info("初始化 GATEWAY HANDLE");
                        List<Handle> handles = new ArrayList<>();
                        Handle handle = new Handle();
                        handle.setId(currentHandleId);
                        handle.setValue(conditionRepair);
                        handles.add(handle);
                        nodeBo.setHandles(handles);
                        NodeBo savedGateway = nodeService.save(nodeBo);
                        gateways.put(srcTopic, savedGateway);
                        log.info("创建连接 INPUT[{}|{}] -> GATEWAY", dsepName, srcTopic);
                        NodeLinkBo linkBo = new NodeLinkBo();
                        linkBo.setSourceId(inputNode.getId());
                        linkBo.setSourceHandle(srcTopic);
                        linkBo.setTargetId(savedGateway.getId());
                        linkBo.setTargetHandle("");
                        nodeLinkService.save(linkBo);
                    }
                    log.info("GATEWAY 节点 {} 创建成功", dsepName);
                    if (StrUtil.isEmpty(execute)) {
                        if (dstTopics != null) {
                            for (String dstTopic : dstTopics) {
                                if (StrUtil.isEmpty(dstTopic)) continue;
                                String outName = ReUtil.get("(^[A-Za-z]+_[A-Za-z]+)_.*", srcTopic, 1);
                                NodeBo outBo = outs.get(outName);
                                log.info("创建连接 GATEWAY -> OUTPUT[{}|{}]", outName, dstTopics);
                                NodeLinkBo linkOutBo = new NodeLinkBo();
                                NodeBo savedGateway = gateways.get(srcTopic);
                                linkOutBo.setSourceId(savedGateway.getId());
                                linkOutBo.setSourceHandle(currentHandleId);
                                linkOutBo.setTargetId(outBo.getId());
                                linkOutBo.setTargetHandle(dstTopic);
                                nodeLinkService.save(linkOutBo);
                            }
                        }
                    }else{
                        String executeRepair = execute
                                .replaceAll("msg\\.", "data.");
                                //.replaceAll("nil", "null")
                                //.replaceAll("\'", "\"");
                        boolean execExist = execNodes.containsKey(dsepName);
                        List<NodeBo> execCache = execNodes.getOrDefault(dsepName, new ArrayList<>());
                        log.info("创建节点 EXEC");
                        NodeBo nodeExecBo = new NodeBo();
                        nodeExecBo.setName(dsepName);
                        nodeExecBo.setJobId(routeConfig.getJobId());
                        nodeExecBo.setNodeType(TransFlowTypeEnum.GATEWAY);
                        nodeExecBo.setPluginId("plugin-json-filter");
                        nodeExecBo.setX(inputNode.getX() + 450);
                        nodeExecBo.setY(0);
                        JSONObject configExec = new JSONObject();
                        nodeExecBo.setConfig(configExec);
                        log.info("初始化 EXEC HANDLE");
                        List<Handle> execHandles = new ArrayList<>();
                        Handle execHandle = new Handle();
                        execHandle.setId("0");
//                        execHandle.setValue(executeRepair +";");
                        execHandle.setValue(executeRepair);
                        execHandles.add(execHandle);
                        nodeExecBo.setHandles(execHandles);
                        NodeBo savedExec = nodeService.save(nodeExecBo);
                        execCache.add(savedExec);
                        if (!execExist) execNodes.put(dsepName, execCache);
                        log.info("EXEC 节点 {} 创建成功", dsepName);
                        log.info("创建连接 GATEWAY -> EXEC");
                        NodeBo savedGateway = gateways.get(srcTopic);
                        NodeLinkBo execlinkBo = new NodeLinkBo();
                        execlinkBo.setSourceId(savedGateway.getId());
                        execlinkBo.setSourceHandle(currentHandleId);
                        execlinkBo.setTargetId(savedExec.getId());
                        execlinkBo.setTargetHandle("");
                        nodeLinkService.save(execlinkBo);
                        if (dstTopics != null) {
                            for (String dstTopic : dstTopics) {
                                if (StrUtil.isEmpty(dstTopic)) continue;
                                String outName = ReUtil.get("(^[A-Za-z]+_[A-Za-z]+)_.*", srcTopic, 1);
                                NodeBo outBo = outs.get(outName);
                                log.info("创建连接 EXEC -> OUTPUT[{}|{}]", outName, dstTopics);
                                NodeLinkBo linkOutBo = new NodeLinkBo();
                                linkOutBo.setSourceId(savedExec.getId());
                                linkOutBo.setSourceHandle("0");
                                linkOutBo.setTargetId(outBo.getId());
                                linkOutBo.setTargetHandle(dstTopic);
                                nodeLinkService.save(linkOutBo);
                            }
                        }
                    }
                    break;
                case "static":
                    if (dstTopics != null) {
                        for (String dstTopic : dstTopics) {
                            if (StrUtil.isEmpty(dstTopic)) continue;
                            String outName = ReUtil.get("(^[A-Za-z]+_[A-Za-z]+)_.*", srcTopic, 1);
                            NodeBo outBo = outs.get(outName);
                            log.info("创建连接 INPUT[{}|{}] -> OUTPUT[{}|{}]", dsepName, srcTopic, outName, dstTopics);
                            NodeLinkBo linkOutBo = new NodeLinkBo();
                            NodeBo srcNode = inputs.get(dsepName);
                            linkOutBo.setSourceId(srcNode.getId());
                            linkOutBo.setSourceHandle(srcTopic);
                            linkOutBo.setTargetId(outBo.getId());
                            linkOutBo.setTargetHandle(dstTopic);
                            nodeLinkService.save(linkOutBo);
                        }
                    }
                    break;
            }
        }
    }
}
