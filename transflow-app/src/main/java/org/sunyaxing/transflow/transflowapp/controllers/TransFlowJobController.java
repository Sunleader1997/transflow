package org.sunyaxing.transflow.transflowapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sunyaxing.transflow.common.ChainStatusEnum;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.transflowapp.common.TransFlowChain;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.EdgeDto;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.NodeDto;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.NodesAndEdgesDto;
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
    public List<JobBo> jobList() {
        return jobService.listAll();
    }

    @PostMapping("/job/save")
    public Boolean jobSave(@RequestBody JobBo jobBo) {
        jobService.save(jobBo);
        return true;
    }

    @PostMapping("/job/build")
    public TransFlowChain<TransFlowInput> jobBuild(@RequestBody JobBo jobBo) {
        return transFlowChainService.buildChain(jobBo.getId());
    }

    @PostMapping("/job/run")
    public Boolean runJob(@RequestBody JobBo jobBo) {
        transFlowChainService.run(jobBo.getId());
        return true;
    }

    @GetMapping("/node/list")
    public List<NodeDto> nodeList(@RequestParam("jobId") String jobId) {
        boolean hasKey = transFlowChainService.hasKey(jobId);
        return nodeService.list(jobId)
                .stream().map(bo -> {
                    NodeDto nodeDto = BoCover.INSTANCE.boToDto(bo);
                    if (hasKey) {
                        TransFlowChain<?> chain = transFlowChainService.get(jobId).getChainByNodeId(bo.getId());
                        nodeDto.getData().setStatus(chain.getStatus());
                    } else {
                        nodeDto.getData().setStatus(ChainStatusEnum.INIT);
                    }
                    return nodeDto;
                }).toList();
    }

    @PostMapping("/node/delete")
    public Boolean nodeDelete(@RequestBody NodeDto nodeDto) {
        nodeService.removeById(nodeDto.getId());
        nodeLinkService.lambdaQuery()
                .eq(NodeLinkEntity::getSourceId, nodeDto.getId())
                .or()
                .eq(NodeLinkEntity::getTargetId, nodeDto.getId())
                .list()
                .forEach(nodeLinkService::removeById);
        return true;
    }

    @PostMapping("/node/save")
    public NodeDto nodeSave(@RequestBody NodeDto nodeDto) {
        NodeBo nodeBo = BoCover.INSTANCE.dtoToBo(nodeDto);
        NodeBo nodeBoRes = nodeService.save(nodeBo);
        return BoCover.INSTANCE.boToDto(nodeBoRes);
    }

    @GetMapping("/node/allForDraw")
    public NodesAndEdgesDto nodeAllForDraw(@RequestParam("jobId") String jobId) {
        List<NodeDto> nodes = nodeList(jobId);
        List<EdgeDto> edges = nodes.stream().map(nodeDto -> {
            return nodeEdges(nodeDto.getId());
        }).flatMap(Collection::stream).toList();
        return new NodesAndEdgesDto(nodes, edges);
    }

    @GetMapping("/node/edges")
    public List<EdgeDto> nodeEdges(@RequestParam("sourceId") String sourceId) {
        return nodeLinkService.lambdaQuery()
                .eq(NodeLinkEntity::getSourceId, sourceId)
                .list()
                .stream().map(BoCover.INSTANCE::entityToBo)
                .map(BoCover.INSTANCE::boToDto)
                .toList();
    }

    @PostMapping("/node/link")
    public EdgeDto nodeSave(@RequestBody NodeLinkBo linkBo) {
        return BoCover.INSTANCE.boToDto(nodeLinkService.save(linkBo));
    }
}
