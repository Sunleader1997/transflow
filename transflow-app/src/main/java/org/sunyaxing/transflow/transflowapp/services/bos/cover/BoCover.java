package org.sunyaxing.transflow.transflowapp.services.bos.cover;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.NodeDto;
import org.sunyaxing.transflow.transflowapp.entity.JobEntity;
import org.sunyaxing.transflow.transflowapp.entity.NodeEntity;
import org.sunyaxing.transflow.transflowapp.entity.NodeLinkEntity;
import org.sunyaxing.transflow.transflowapp.services.bos.JobBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeLinkBo;

@Mapper(uses = {CommonCover.class})
public interface BoCover {
    BoCover INSTANCE = Mappers.getMapper(BoCover.class);

    @Mappings({
    })
    JobBo entityToBo(JobEntity jobEntity);

    @Mappings({
            @Mapping(source = "id", target = "id", qualifiedByName = "generateIfNull")
    })
    JobEntity boToEntity(JobBo jobBo);

    @Mappings({
            @Mapping(source = "config", target = "config", qualifiedByName = "strToProperties")
    })
    NodeBo entityToBo(NodeEntity nodeEntity);

    @Mappings({
    })
    NodeDto boToDto(NodeBo nodeBo);

    @Mappings({
            @Mapping(source = "id", target = "id", qualifiedByName = "generateIfNull"),
            @Mapping(source = "config", target = "config", qualifiedByName = "propertiesToStr")
    })
    NodeEntity boToEntity(NodeBo nodeBo);

    @Mappings({
    })
    NodeLinkBo entityToBo(NodeLinkEntity nodeBo);

    @Mappings({
            @Mapping(source = "id", target = "id", qualifiedByName = "generateIfNull"),
    })
    NodeLinkEntity boToEntity(NodeLinkBo nodeBo);
}
