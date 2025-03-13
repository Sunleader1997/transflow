package org.sunyaxing.transflow.transflowapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sunyaxing.transflow.common.ChainStatusEnum;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.transflowapp.common.TransFlowChain;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.NodeDto;
import org.sunyaxing.transflow.transflowapp.services.JobService;
import org.sunyaxing.transflow.transflowapp.services.NodeLinkService;
import org.sunyaxing.transflow.transflowapp.services.NodeService;
import org.sunyaxing.transflow.transflowapp.services.TransFlowChainService;
import org.sunyaxing.transflow.transflowapp.services.bos.JobBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeLinkBo;
import org.sunyaxing.transflow.transflowapp.services.bos.cover.BoCover;

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
    public List<NodeDto> nodeList(@RequestParam("jobId") Long jobId) {
        boolean hasKey = transFlowChainService.hasKey(jobId);
        return nodeService.list(jobId)
                .stream().map(bo -> {
                    NodeDto nodeDto = BoCover.INSTANCE.boToDto(bo);
                    if (hasKey) {
                        TransFlowChain<?> chain = transFlowChainService.get(jobId).getChainByNodeId(bo.getId());
                        nodeDto.setStatus(chain.getStatus());
                    } else {
                        nodeDto.setStatus(ChainStatusEnum.INIT);
                    }
                    return nodeDto;
                }).toList();
    }

    @PostMapping("/node/save")
    public Boolean nodeSave(@RequestBody NodeBo nodeBo) {
        nodeService.save(nodeBo);
        return true;
    }

    @PostMapping("/node/link")
    public Boolean nodeSave(@RequestBody NodeLinkBo linkBo) {
        nodeLinkService.save(linkBo);
        return true;
    }
}
