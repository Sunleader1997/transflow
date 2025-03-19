package org.sunyaxing.transflow.pluginkafkainput;

import org.sunyaxing.transflow.common.ano.JobParamItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;
import org.sunyaxing.transflow.plugins.TransFlowInputPlugin;

@ScopeContentCheck({
        @JobParamItem(field = "group-id", label = "GroupId", defaultValue = "transflow"),
        @JobParamItem(field = "bootstrap-servers", label = "节点(ip:port)", defaultValue = "127.0.0.1:9093"),
        @JobParamItem(field = "max-poll-records", label = "批量拉取大小", defaultValue = "1000")
})
public class KafkaInputPlugin extends TransFlowInputPlugin {
}
