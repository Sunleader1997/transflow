package org.sunyaxing.transflow.pluginkafkaoutput;

import org.sunyaxing.transflow.common.ano.JobParamItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;
import org.sunyaxing.transflow.plugins.TransFlowOutputPlugin;

@ScopeContentCheck({
        @JobParamItem(field = "topic", label = "输出Topic", defaultValue = "topic"),
        @JobParamItem(field = "bootstrap-servers", label = "节点(ip:port)", defaultValue = "127.0.0.1:9093"),
        @JobParamItem(field = "topics", label = "输出节点", type = "handlers", javaType = String[].class, defaultValue = "[\"topic1\"]")
})
public class KafkaOutputPlugin extends TransFlowOutputPlugin {
}
