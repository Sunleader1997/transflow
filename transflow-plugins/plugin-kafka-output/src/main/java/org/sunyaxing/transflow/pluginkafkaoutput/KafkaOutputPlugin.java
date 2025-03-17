package org.sunyaxing.transflow.pluginkafkaoutput;

import org.sunyaxing.transflow.common.ano.JobParamItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;
import org.sunyaxing.transflow.plugins.TransFlowOutputPlugin;

@ScopeContentCheck({
        @JobParamItem(field = "bootstrap-servers", label = "节点(ip:port)", defaultValue = "127.0.0.1:9093"),
})
public class KafkaOutputPlugin extends TransFlowOutputPlugin {
}
