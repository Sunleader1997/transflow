package org.sunyaxing.transflow.transflowapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import org.sunyaxing.transflow.transflowapp.common.ChainManager;
import org.sunyaxing.transflow.transflowapp.common.Result;
import org.sunyaxing.transflow.transflowapp.common.TransFlowChain;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.ChainStatus;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.EdgeDto;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.NodeDto;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.NodesAndEdgesDto;
import org.sunyaxing.transflow.transflowapp.entity.NodeEntity;
import org.sunyaxing.transflow.transflowapp.entity.NodeLinkEntity;
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
import java.util.stream.Collectors;

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
    public Result<Boolean> jobBuild(@RequestBody JobBo jobBo) {
        transFlowChainService.buildChain(jobBo.getId());
        return Result.success(true);
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
        List<NodeDto> res = nodeService.list(jobId)
                .stream().map(BoCover.INSTANCE::boToDto).collect(Collectors.toList());
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
        }).flatMap(Collection::stream).collect(Collectors.toList());
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
                .collect(Collectors.toList());
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
        TransFlowChain<?> chain = ChainManager.getChainCache(nodeId);
        if (chain != null) {
            ExtensionLifecycle extension = chain.getCurrentNode();
            ChainStatus chainStatus = new ChainStatus();
            chainStatus.setRemainNumb(extension.getRemainingDataSize());
            chainStatus.setRecNumb(extension.getRecNumb());
            chainStatus.setSendNumb(extension.getSendNumb());
            return Result.success(chainStatus);
        }
        return Result.success(new ChainStatus());
    }
}
