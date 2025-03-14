package org.sunyaxing.transflow.transflowapp.services;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sunyaxing.transflow.transflowapp.entity.JobEntity;
import org.sunyaxing.transflow.transflowapp.entity.NodeEntity;
import org.sunyaxing.transflow.transflowapp.repositories.JobRepository;
import org.sunyaxing.transflow.transflowapp.repositories.NodeRepository;
import org.sunyaxing.transflow.transflowapp.services.bos.JobBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.cover.BoCover;

import java.util.List;
import java.util.Objects;

@Service
public class NodeService extends ServiceImpl<NodeRepository, NodeEntity> {
    @Autowired
    private PluginManager pluginManager;
    @Autowired
    private JobRepository jobRepository;

    public NodeBo boById(String nodeId) {
        NodeEntity nodeEntity = this.getById(nodeId);
        return BoCover.INSTANCE.entityToBo(nodeEntity);
    }

    public void save(NodeBo nodeBo) {
        var plugin = pluginManager.getPlugin(nodeBo.getPluginId());
        Assert.notNull(plugin,"插件不存在");
        var job = jobRepository.selectById(nodeBo.getJobId());
        Assert.notNull(job,"空间不存在");
        NodeEntity nodeEntity = BoCover.INSTANCE.boToEntity(nodeBo);
        if (nodeBo.getId() == null) {
            this.save(nodeEntity);
        } else {
            this.updateById(nodeEntity);
        }
    }

    public List<NodeBo> list(String jobId) {
        return this.lambdaQuery()
                .eq(NodeEntity::getJobId, jobId)
                .list()
                .stream()
                .map(BoCover.INSTANCE::entityToBo)
                .toList();
    }

}
