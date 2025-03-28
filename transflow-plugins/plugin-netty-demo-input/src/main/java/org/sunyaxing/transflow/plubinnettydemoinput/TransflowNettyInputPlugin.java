package org.sunyaxing.transflow.plubinnettydemoinput;

import org.sunyaxing.transflow.common.ano.JobParamItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;
import org.sunyaxing.transflow.plugins.TransFlowInputPlugin;

@ScopeContentCheck({
        @JobParamItem(field = "host", label = "HOST", defaultValue = "0.0.0.0"),
        @JobParamItem(field = "port", label = "端口", defaultValue = "8080"),
})
public class TransflowNettyInputPlugin extends TransFlowInputPlugin {
}
