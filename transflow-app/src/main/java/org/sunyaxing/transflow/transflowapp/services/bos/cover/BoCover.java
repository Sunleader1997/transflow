package org.sunyaxing.transflow.transflowapp.services.bos.cover;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.sunyaxing.transflow.transflowapp.entity.JobEntity;
import org.sunyaxing.transflow.transflowapp.entity.NodeEntity;
import org.sunyaxing.transflow.transflowapp.services.bos.JobBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;

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
            @Mapping(source = "config", target = "config", qualifiedByName = "strToJSON")
    })
    NodeBo entityToBo(NodeEntity nodeEntity);

    @Mappings({
            @Mapping(source = "id", target = "id", qualifiedByName = "generateIfNull"),
            @Mapping(source = "config", target = "config", qualifiedByName = "jsonToStr")
    })
    NodeEntity boToEntity(NodeBo nodeBo);
}
