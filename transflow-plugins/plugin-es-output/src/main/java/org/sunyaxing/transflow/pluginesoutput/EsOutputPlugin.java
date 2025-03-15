package org.sunyaxing.transflow.pluginesoutput;

import org.sunyaxing.transflow.common.ano.JobParamItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;
import org.sunyaxing.transflow.plugins.TransFlowOutputPlugin;

@ScopeContentCheck({
        @JobParamItem(field = "index-name", label = "index-name", required = true),
        @JobParamItem(field = "host", label = "host", required = true),
        @JobParamItem(field = "port", label = "port", required = true),
        @JobParamItem(field = "username", label = "username", required = true),
        @JobParamItem(field = "password", label = "password", required = true)
})
public class EsOutputPlugin extends TransFlowOutputPlugin {
}
