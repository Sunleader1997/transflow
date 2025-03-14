package org.sunyaxing.transflow.transflowapp.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.sunyaxing.transflow.transflowapp.entity.NodeLinkEntity;
import org.sunyaxing.transflow.transflowapp.repositories.NodeLinkRepository;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeLinkBo;
import org.sunyaxing.transflow.transflowapp.services.bos.cover.BoCover;

import java.util.List;

@Service
public class NodeLinkService extends ServiceImpl<NodeLinkRepository, NodeLinkEntity> {
    public void save(NodeLinkBo linkBo) {
        NodeLinkEntity linkEntity = BoCover.INSTANCE.boToEntity(linkBo);
        if (linkBo.getId() == null) {
            this.save(linkEntity);
        } else {
            this.updateById(linkEntity);
        }
    }

    public List<NodeLinkBo> findLinksBySource(String sourceId) {
        return this.lambdaQuery()
                .eq(NodeLinkEntity::getSourceId, sourceId)
                .list()
                .stream().map(BoCover.INSTANCE::entityToBo)
                .toList();
    }
}
