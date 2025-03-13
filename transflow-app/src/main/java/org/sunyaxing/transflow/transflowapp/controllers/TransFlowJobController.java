package org.sunyaxing.transflow.transflowapp.controllers;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sunyaxing.transflow.transflowapp.common.TransFlowChain;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.transflowapp.services.JobService;
import org.sunyaxing.transflow.transflowapp.services.NodeLinkService;
import org.sunyaxing.transflow.transflowapp.services.NodeService;
import org.sunyaxing.transflow.transflowapp.services.TransFlowChainService;
import org.sunyaxing.transflow.transflowapp.services.bos.JobBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeLinkBo;

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
    public List<NodeBo> nodeList(@RequestParam("jobId") Long jobId) {
        return nodeService.list(jobId);
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
