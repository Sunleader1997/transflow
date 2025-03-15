package org.sunyaxing.transflow.transflowapp.controllers.dtos;

import lombok.Data;

import java.util.List;

@Data
public class NodesAndEdgesDto {
    private List<NodeDto> nodes;
    private List<EdgeDto> edges;

    public NodesAndEdgesDto(List<NodeDto> nodes, List<EdgeDto> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }
}
