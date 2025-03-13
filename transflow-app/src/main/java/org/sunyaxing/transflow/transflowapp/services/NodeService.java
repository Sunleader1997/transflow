package org.sunyaxing.transflow.transflowapp.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.sunyaxing.transflow.transflowapp.entity.NodeEntity;
import org.sunyaxing.transflow.transflowapp.repositories.NodeRepository;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.cover.BoCover;

import java.util.List;

@Service
public class NodeService extends ServiceImpl<NodeRepository, NodeEntity> {

    public void save(NodeBo nodeBo) {
        NodeEntity nodeEntity = BoCover.INSTANCE.boToEntity(nodeBo);
        if (nodeBo.getId() == null) {
            this.save(nodeEntity);
        } else {
            this.updateById(nodeEntity);
        }
    }

    public List<NodeBo> list(Long jobId) {
        return this.lambdaQuery()
                .eq(NodeEntity::getJobId, jobId)
                .list()
                .stream()
                .map(BoCover.INSTANCE::entityToBo)
                .toList();
    }

}
