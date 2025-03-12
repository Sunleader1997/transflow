package org.sunyaxing.transflow.transflowapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sunyaxing.transflow.transflowapp.entity.TransFlowJobEntity;
import org.sunyaxing.transflow.transflowapp.services.TransFlowJobService;

import java.util.List;

@RestController
@RequestMapping("/transflow")
public class TransFlowJobController {

    @Autowired
    private TransFlowJobService transFlowJobService;

    @GetMapping("/list")
    public List<TransFlowJobEntity> list() {
        return transFlowJobService.list();
    }

    @PostMapping("/save")
    public Boolean save(@RequestBody TransFlowJobEntity transFlowJobEntity) {
        boolean save = transFlowJobService.save(transFlowJobEntity);
        return save;
    }
}
