package org.sunyaxing.transflow.transflowapp.controllers;

import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.PluginListDto;

import java.util.List;

@RestController
@RequestMapping("/plugins")
public class PluginsController {
    @Autowired
    private PluginManager pluginManager;

    @GetMapping("/list")
    public List<PluginListDto> getPlugins() {
        return pluginManager.getPlugins().stream().map(pluginWrapper -> {
            return new PluginListDto(
                    pluginWrapper.getPluginId(),
                    pluginWrapper.getPluginState().toString()
            );
        }).toList();
    }

    @PostMapping("/runPlugin")
    public String runPlugin(@RequestParam("pluginId") String pluginId) {
        pluginManager.startPlugin(pluginId);
        return "success";
    }

    @PostMapping("/getExt")
    public String getExt() {
        List<TransFlowInput> transFlowInputs = pluginManager.getExtensions(TransFlowInput.class);
        return transFlowInputs.toString();
    }
}
