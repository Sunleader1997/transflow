package org.sunyaxing.transflow.transflowapp.config.porperties;

import lombok.Data;

@Data
public class Route {
    private String srcTopic;
    private String[] dstTopic;
    private String type;
    private String conditionIf;
    private String execute;
}
