package org.sunyaxing.transflow.transflowapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import org.sunyaxing.transflow.transflowapp.common.Result;
import org.sunyaxing.transflow.transflowapp.common.TransFlowChain;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.ChainStatus;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.EdgeDto;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.NodeDto;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.NodesAndEdgesDto;
import org.sunyaxing.transflow.transflowapp.entity.NodeEntity;
import org.sunyaxing.transflow.transflowapp.entity.NodeLinkEntity;
import org.sunyaxing.transflow.transflowapp.reactor.TransFlowRunnable;
import org.sunyaxing.transflow.transflowapp.services.JobService;
import org.sunyaxing.transflow.transflowapp.services.NodeLinkService;
import org.sunyaxing.transflow.transflowapp.services.NodeService;
import org.sunyaxing.transflow.transflowapp.services.TransFlowChainService;
import org.sunyaxing.transflow.transflowapp.services.bos.JobBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeLinkBo;
import org.sunyaxing.transflow.transflowapp.services.bos.cover.BoCover;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/transflow")
public class TransFlowJobController {

    @Autowired
    private JobService jobService;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private NodeLinkService nodeLinkService;
    @Autowired
    private TransFlowChainService transFlowChainService;

    @GetMapping("/job/list")
    public Result<List<JobBo>> jobList() {
        List<JobBo> res = jobService.listAll();
        return Result.success(res);
    }

    @PostMapping("/job/save")
    public Result<Boolean> jobSave(@RequestBody JobBo jobBo) {
        jobService.save(jobBo);
        return Result.success(true);
    }

    @PostMapping("/job/delete")
    public Result<Boolean> deleteJob(@RequestBody JobBo jobBo) {
        jobService.removeById(jobBo.getId());
        nodeService.lambdaQuery()
                .eq(NodeEntity::getJobId, jobBo.getId())
                .list()
                .forEach(nodeEntity -> {
                    nodeService.removeById(nodeEntity.getId());
                    nodeLinkService.lambdaQuery()
                            .eq(NodeLinkEntity::getSourceId, nodeEntity.getId())
                            .or()
                            .eq(NodeLinkEntity::getTargetId, nodeEntity.getId())
                            .list()
                            .forEach(nodeLinkService::removeById);
                });
        return Result.success(true);
    }

    @PostMapping("/job/build")
    public Result<TransFlowChain<TransFlowInput>> jobBuild(@RequestBody JobBo jobBo) {
        TransFlowChain<TransFlowInput> res = transFlowChainService.buildChain(jobBo.getId());
        res.dispose();
        return Result.success(res);
    }

    @PostMapping("/job/run")
    public Result<Boolean> runJob(@RequestBody JobBo jobBo) {
        transFlowChainService.run(jobBo.getId());
        return Result.success(true);
    }

    @PostMapping("/job/stop")
    public Result<Boolean> runStop(@RequestBody JobBo jobBo) {
        transFlowChainService.stop(jobBo.getId());
        return Result.success(true);
    }

    @GetMapping("/node/list")
    public Result<List<NodeDto>> nodeList(@RequestParam("jobId") String jobId) {
        boolean hasKey = transFlowChainService.hasKey(jobId);
        List<NodeDto> res = nodeService.list(jobId)
                .stream().map(bo -> {
                    NodeDto nodeDto = BoCover.INSTANCE.boToDto(bo);
                    // TODO 暂时无法获取到节点状态
//                    if (hasKey) {
//                        TransFlowChain<?> chain = transFlowChainService.get(jobId).getChainByNodeId(bo.getId());
//                        nodeDto.getData().setStatus(chain.getStatus());
//                    } else {
//                        nodeDto.getData().setStatus(ChainStatusEnum.INIT);
//                    }
                    return nodeDto;
                }).toList();
        return Result.success(res);
    }

    @PostMapping("/node/delete")
    public Result<Boolean> nodeDelete(@RequestBody NodeDto nodeDto) {
        nodeService.removeById(nodeDto.getId());
        nodeLinkService.lambdaQuery()
                .eq(NodeLinkEntity::getSourceId, nodeDto.getId())
                .or()
                .eq(NodeLinkEntity::getTargetId, nodeDto.getId())
                .list()
                .forEach(nodeLinkService::removeById);
        return Result.success(true);
    }

    @PostMapping("/node/save")
    public Result<NodeDto> nodeSave(@RequestBody NodeDto nodeDto) {
        NodeBo nodeBo = BoCover.INSTANCE.dtoToBo(nodeDto);
        NodeBo nodeBoRes = nodeService.save(nodeBo);
        NodeDto res = BoCover.INSTANCE.boToDto(nodeBoRes);
        return Result.success(res);
    }

    @GetMapping("/node/allForDraw")
    public Result<NodesAndEdgesDto> nodeAllForDraw(@RequestParam("jobId") String jobId) {
        List<NodeDto> nodes = nodeList(jobId).getData();
        List<EdgeDto> edges = nodes.stream().map(nodeDto -> {
            return nodeEdges(nodeDto.getId()).getData();
        }).flatMap(Collection::stream).toList();
        NodesAndEdgesDto res = new NodesAndEdgesDto(nodes, edges);
        return Result.success(res);
    }

    @GetMapping("/node/edges")
    public Result<List<EdgeDto>> nodeEdges(@RequestParam("sourceId") String sourceId) {
        List<EdgeDto> res = nodeLinkService.lambdaQuery()
                .eq(NodeLinkEntity::getSourceId, sourceId)
                .list()
                .stream().map(BoCover.INSTANCE::entityToBo)
                .map(BoCover.INSTANCE::boToDto)
                .toList();
        return Result.success(res);
    }

    @PostMapping("/node/link")
    public Result<EdgeDto> nodeSave(@RequestBody NodeLinkBo linkBo) {
        EdgeDto res = BoCover.INSTANCE.boToDto(nodeLinkService.save(linkBo));
        return Result.success(res);
    }

    @PostMapping("/node/unlink")
    public Result<Boolean> unlink(@RequestBody NodeLinkBo linkBo) {
        nodeLinkService.removeById(linkBo.getId());
        return Result.success(true);
    }

    @GetMapping("/node/status")
    public Result<ChainStatus> unlink(@RequestParam("nodeId") String nodeId) {
        NodeBo nodeBo = nodeService.boById(nodeId);
        String jobId = nodeBo.getJobId();
        TransFlowRunnable transFlowRunnable = transFlowChainService.get(jobId);
        if (transFlowRunnable != null) {
            TransFlowChain<TransFlowInput> rootChain = transFlowRunnable.getChain();
            ExtensionLifecycle extension = rootChain.getExtension(nodeId);
            if(Objects.nonNull(extension)){
                ChainStatus chainStatus = new ChainStatus();
                chainStatus.setRemainNumb(extension.getRemainingDataSize());
                chainStatus.setRecNumb(extension.getRecNumb());
                chainStatus.setSendNumb(extension.getSendNumb());
                return Result.success(chainStatus);
            }
        }
        return Result.success(new ChainStatus());
    }
}
