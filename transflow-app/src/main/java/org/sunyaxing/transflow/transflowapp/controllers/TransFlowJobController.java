package org.sunyaxing.transflow.transflowapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sunyaxing.transflow.transflowapp.entity.JobEntity;
import org.sunyaxing.transflow.transflowapp.services.JobService;

import java.util.List;

@RestController
@RequestMapping("/transflow")
public class TransFlowJobController {

    @Autowired
    private JobService jobService;

    @GetMapping("/list")
    public List<JobEntity> list() {
        return jobService.list();
    }

    @PostMapping("/save")
    public Boolean save(@RequestBody JobEntity jobEntity) {
        boolean save = jobService.save(jobEntity);
        return save;
    }
}
