package org.sunyaxing.transflow.transflowapp.controllers;

import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sunyaxing.transflow.transflowapp.entity.JobEntity;
import org.sunyaxing.transflow.transflowapp.services.JobService;
import org.sunyaxing.transflow.transflowapp.services.NodeService;
import org.sunyaxing.transflow.transflowapp.services.bos.JobBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;

import java.util.List;

@RestController
@RequestMapping("/transflow")
public class TransFlowJobController {

    @Autowired
    private JobService jobService;
    @Autowired
    private NodeService nodeService;


    @GetMapping("/job/list")
    public List<JobEntity> jobList() {
        return jobService.list();
    }

    @PostMapping("/job/save")
    public Boolean jobSave(@RequestBody JobBo jobBo) {
        jobService.save(jobBo);
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
}
