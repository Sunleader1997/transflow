package org.sunyaxing.transflow.pluginkafkainput;

import org.sunyaxing.transflow.common.ano.JobParamItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;
import org.sunyaxing.transflow.plugins.TransFlowInputPlugin;

@ScopeContentCheck({
        @JobParamItem(field = "group-id", label = "group-id", required = true),
        @JobParamItem(field = "topics", label = "topic", required = true),
        @JobParamItem(field = "bootstrap-servers", label = "bootstrap-servers", required = true),
        @JobParamItem(field = "max-poll-records", label = "max-poll-records", required = true)
})
public class KafkaInputPlugin extends TransFlowInputPlugin {
}
