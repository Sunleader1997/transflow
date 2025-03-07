package org.sunyaxing.transflow.transflowapp.controllers;

import cn.hutool.core.thread.ThreadUtil;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.transflowapp.config.DirFileWatcher;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.PluginListDto;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/plugins")
public class PluginsController {
    private static final Logger log = LoggerFactory.getLogger(PluginsController.class);
    @Autowired
    private PluginManager pluginManager;
    @Autowired
    private DirFileWatcher dirFileWatcher;

    @GetMapping("/list")
    public List<PluginListDto> getPlugins() {
        return pluginManager.getPlugins().stream().map(pluginWrapper -> {
            return new PluginListDto(
                    pluginWrapper.getPluginId(),
                    pluginWrapper.getPluginState().toString()
            );
        }).toList();
    }

    @PostMapping("/poll")
    public String getExt() {
        HashMap<String, List<TransFlowInput>> transFlowInputList = dirFileWatcher.getPluginInputExtensions();
        transFlowInputList.values().forEach(transFlowInputs -> {
            transFlowInputs.forEach(transFlowInput -> {
                log.info(transFlowInput.dequeue());
            });
        });
        return "transFlowInputs.toString()";
    }
}
