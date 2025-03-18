package org.sunyaxing.transflow.transflowapp.services;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.pf4j.Plugin;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sunyaxing.transflow.transflowapp.config.JobConfigProperties;
import org.sunyaxing.transflow.transflowapp.entity.NodeEntity;
import org.sunyaxing.transflow.transflowapp.repositories.JobRepository;
import org.sunyaxing.transflow.transflowapp.repositories.NodeRepository;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.cover.BoCover;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NodeService extends ServiceImpl<NodeRepository, NodeEntity> {
    @Autowired
    private PluginManager pluginManager;
    @Autowired
    private JobRepository jobRepository;

    public NodeBo boById(String nodeId) {
        NodeEntity nodeEntity = this.getById(nodeId);
        NodeBo nodeBo = BoCover.INSTANCE.entityToBo(nodeEntity);
        List<JobConfigProperties> properties = this.parseConfig(nodeBo);
        nodeBo.setProperties(properties);
        return nodeBo;
    }

    public NodeBo save(NodeBo nodeBo) {
        var job = jobRepository.selectById(nodeBo.getJobId());
        Assert.notNull(job, "空间不存在");
        this.parseConfig(nodeBo);
        NodeEntity nodeEntity = BoCover.INSTANCE.boToEntity(nodeBo);
        if (nodeBo.getId() == null) {
            this.save(nodeEntity);
        } else {
            this.updateById(nodeEntity);
        }
        NodeBo res = BoCover.INSTANCE.entityToBo(nodeEntity);
        List<JobConfigProperties> properties = this.parseConfig(res);
        res.setProperties(properties);
        return res;
    }

    public List<NodeBo> list(String jobId) {
        return this.lambdaQuery()
                .eq(NodeEntity::getJobId, jobId)
                .list()
                .stream()
                .map(entity -> {
                    NodeBo nodeBo = BoCover.INSTANCE.entityToBo(entity);
                    List<JobConfigProperties> properties = this.parseConfig(nodeBo);
                    nodeBo.setProperties(properties);
                    return nodeBo;
                })
                .collect(Collectors.toList());
    }

    public List<JobConfigProperties> parseConfig(NodeBo nodeBo) {
        PluginWrapper pluginWp = pluginManager.getPlugin(nodeBo.getPluginId());
        Assert.notNull(pluginWp, "插件不存在");
        Plugin plugin = pluginWp.getPlugin();
        List<JobConfigProperties> configList = JobConfigProperties.getJobProperties(plugin);
        configList.forEach(jobConfigProperties -> {
            if (!nodeBo.getConfig().containsKey(jobConfigProperties.getKey())) {
                nodeBo.getConfig().put(
                        jobConfigProperties.getKey(),
                        parseDefaultValue(jobConfigProperties.getJavaType(), jobConfigProperties.getDefaultValue())
                );
            }
        });
        return configList;
    }

    private static Object parseDefaultValue(Class<?> javaType, String defaultValue) {
        if (javaType == String.class) {
            return defaultValue;
        } else if (javaType == Integer.class || javaType == int.class) {
            return Integer.parseInt(defaultValue);
        } else if (javaType == Long.class || javaType == long.class) {
            return Long.parseLong(defaultValue);
        } else if (javaType == Double.class || javaType == double.class) {
            return Double.parseDouble(defaultValue);
        } else if (javaType == Boolean.class || javaType == boolean.class) {
            return Boolean.parseBoolean(defaultValue);
        } else if (javaType.isArray()) {
            // 支持数组类型
            return JSONArray.parseArray(defaultValue);
        } else {
            throw new IllegalArgumentException("Failed to parse default value: " + defaultValue);
        }
    }
}
