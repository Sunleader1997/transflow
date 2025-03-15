package org.sunyaxing.transflow.pluginkafkaoutput;

import org.sunyaxing.transflow.common.ano.JobParamItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;
import org.sunyaxing.transflow.plugins.TransFlowOutputPlugin;

@ScopeContentCheck({
        @JobParamItem(field = "topic", label = "topic", required = true),
        @JobParamItem(field = "bootstrap-servers", label = "bootstrap-servers", required = true)
})
public class KafkaOutputPlugin extends TransFlowOutputPlugin {
}
