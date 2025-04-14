package org.sunyaxing.transflow.transflowapp.services.bos.cover;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.EdgeDto;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.JobDto;
import org.sunyaxing.transflow.transflowapp.controllers.dtos.NodeDto;
import org.sunyaxing.transflow.transflowapp.entity.JobEntity;
import org.sunyaxing.transflow.transflowapp.entity.NodeEntity;
import org.sunyaxing.transflow.transflowapp.entity.NodeLinkEntity;
import org.sunyaxing.transflow.transflowapp.services.bos.JobBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeBo;
import org.sunyaxing.transflow.transflowapp.services.bos.NodeLinkBo;

import java.util.List;

@Mapper(uses = {CommonCover.class})
public interface BoCover {
    BoCover INSTANCE = Mappers.getMapper(BoCover.class);

    @Mappings({
    })
    JobDto boToDto(JobBo jobBo);

    @Mappings({
    })
    JobBo entityToBo(JobEntity jobEntity);

    @Mappings({
            @Mapping(source = "id", target = "id", qualifiedByName = "generateIfNull")
    })
    JobEntity boToEntity(JobBo jobBo);

    @Mappings({
            @Mapping(source = "config", target = "config", qualifiedByName = "strToProperties"),
            @Mapping(source = "handles", target = "handles", qualifiedByName = "stringToHandles")
    })
    NodeBo entityToBo(NodeEntity nodeEntity);

    @Mappings({
            @Mapping(source = "nodeBo", target = "position", qualifiedByName = "position"),
            @Mapping(source = "name", target = "data.name"),
            @Mapping(source = "jobId", target = "data.jobId"),
            @Mapping(source = "pluginId", target = "data.pluginId"),
            @Mapping(source = "nodeType", target = "data.nodeType"),
            @Mapping(source = "config", target = "data.config", qualifiedByName = "useSet"),
            @Mapping(source = "nodeType", target = "type", qualifiedByName = "nodeTypeToString"),
            @Mapping(source = "properties", target = "data.properties", qualifiedByName = "useSet"),
            @Mapping(source = "handles", target = "data.handles", qualifiedByName = "useSet")
    })
    NodeDto boToDto(NodeBo nodeBo);

    @Mappings({
            @Mapping(target = "x", source = "position", qualifiedByName = "positionX"),
            @Mapping(target = "y", source = "position", qualifiedByName = "positionY"),
            @Mapping(target = "name", source = "data.name"),
            @Mapping(target = "jobId", source = "data.jobId"),
            @Mapping(target = "pluginId", source = "data.pluginId"),
            @Mapping(target = "config", source = "data.config", qualifiedByName = "useSet"),
            @Mapping(target = "nodeType", source = "type", qualifiedByName = "stringToNodeType"),
            @Mapping(target = "handles", source = "data.handles", qualifiedByName = "emptyIfNull")
    })
    NodeBo dtoToBo(NodeDto nodeDto);

    @Mappings({
            @Mapping(source = "id", target = "id", qualifiedByName = "generateIfNull"),
            @Mapping(source = "config", target = "config", qualifiedByName = "propertiesToStr"),
            @Mapping(source = "handles", target = "handles", qualifiedByName = "handleToString")
    })
    NodeEntity boToEntity(NodeBo nodeBo);

    @Mappings({
    })
    NodeLinkBo entityToBo(NodeLinkEntity nodeBo);

    @Mappings({
            @Mapping(source = "id", target = "id", qualifiedByName = "generateIfNull"),
    })
    NodeLinkEntity boToEntity(NodeLinkBo nodeBo);

    @Mappings({
            @Mapping(source = "targetId", target = "target"),
            @Mapping(source = "sourceId", target = "source"),
    })
    EdgeDto boToDto(NodeLinkBo nodeBo);

    @Named("position")
    default NodeDto.Position position(NodeBo nodeBo) {
        NodeDto.Position position = new NodeDto.Position();
        position.setX(nodeBo.getX());
        position.setY(nodeBo.getY());
        return position;
    }

    @Named("positionX")
    default Integer positionX(NodeDto.Position position) {
        return position.getX();
    }

    @Named("positionY")
    default Integer positionY(NodeDto.Position position) {
        return position.getY();
    }
    @Named("stringToHandles")
    default List<Handle> stringToHandles(String arrayStr) {
        return JSONArray.parseArray(arrayStr, Handle.class);
    }
    @Named("handleToString")
    default String handleToString(List<Handle> handles) {
        return JSONArray.toJSONString(handles);
    }
}
