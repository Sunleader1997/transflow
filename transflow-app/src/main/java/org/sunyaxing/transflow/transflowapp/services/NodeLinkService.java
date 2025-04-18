package org.sunyaxing.transflow.transflowapp.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.sunyaxing.transflow.transflowapp.entity.NodeLinkEntity;
import org.sunyaxing.transflow.transflowapp.repositories.NodeLinkRepository;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeLinkBo;
import org.sunyaxing.transflow.transflowapp.services.bos.cover.BoCover;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NodeLinkService extends ServiceImpl<NodeLinkRepository, NodeLinkEntity> {
    public NodeLinkBo save(NodeLinkBo linkBo) {
        NodeLinkEntity linkEntity = BoCover.INSTANCE.boToEntity(linkBo);
        if (linkBo.getId() == null) {
            this.save(linkEntity);
        } else {
            this.updateById(linkEntity);
        }
        return BoCover.INSTANCE.entityToBo(linkEntity);
    }

    public List<NodeLinkBo> findLinksBySource(String sourceId) {
        return this.lambdaQuery()
                .eq(NodeLinkEntity::getSourceId, sourceId)
                .list()
                .stream().map(BoCover.INSTANCE::entityToBo)
                .collect(Collectors.toList());
    }
}
