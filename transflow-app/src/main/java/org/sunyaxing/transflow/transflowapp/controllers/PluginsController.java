package org.sunyaxing.transflow.transflowapp.controllers;

import org.pf4j.Plugin;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sunyaxing.transflow.plugins.TransFlowFilterPlugin;
import org.sunyaxing.transflow.plugins.TransFlowInputPlugin;
import org.sunyaxing.transflow.plugins.TransFlowOutputPlugin;
import org.sunyaxing.transflow.transflowapp.config.DirFileWatcher;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.PluginListDto;

import java.util.List;

@RestController
@RequestMapping("/transflow/plugins")
public class PluginsController {
    private static final Logger log = LoggerFactory.getLogger(PluginsController.class);
    @Autowired
    private PluginManager pluginManager;
    @Autowired
    private DirFileWatcher dirFileWatcher;

    @GetMapping("/list")
    public List<PluginListDto> getPlugins() {
        return pluginManager.getPlugins().stream().map(pluginWrapper -> {
            PluginListDto pluginListDto = new PluginListDto();
            pluginListDto.setId(pluginWrapper.getPluginId());
            pluginListDto.setState(pluginWrapper.getPluginState().toString());
            pluginListDto.setDescription(pluginWrapper.getDescriptor().getPluginDescription());
            pluginListDto.setVersion(pluginWrapper.getDescriptor().getVersion());
            Plugin plugin = pluginWrapper.getPlugin();
            if(plugin instanceof TransFlowInputPlugin){
                pluginListDto.setType("input");
            } else if (plugin instanceof TransFlowFilterPlugin) {
                pluginListDto.setType("filter");
            } else if (plugin instanceof TransFlowOutputPlugin) {
                pluginListDto.setType("output");
            }
            return pluginListDto;
        }).toList();
    }

}
