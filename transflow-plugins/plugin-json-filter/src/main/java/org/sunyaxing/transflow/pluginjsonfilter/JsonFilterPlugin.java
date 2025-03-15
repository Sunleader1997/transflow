package org.sunyaxing.transflow.pluginjsonfilter;

import org.sunyaxing.transflow.common.ano.JobParamItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;
import org.sunyaxing.transflow.plugins.TransFlowFilterPlugin;

@ScopeContentCheck(
        @JobParamItem(field = "script", label = "script", required = false, type = "code")
)
public class JsonFilterPlugin extends TransFlowFilterPlugin {
}
