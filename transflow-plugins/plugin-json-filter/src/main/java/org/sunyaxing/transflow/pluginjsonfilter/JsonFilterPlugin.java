package org.sunyaxing.transflow.pluginjsonfilter;

import org.sunyaxing.transflow.common.ano.JobParamItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;
import org.sunyaxing.transflow.plugins.TransFlowFilterPlugin;

@ScopeContentCheck(
        @JobParamItem(field = "script", label = "Script", type = "code", required = false, defaultValue = "return data.containsKey(\"param1\");")
)
public class JsonFilterPlugin extends TransFlowFilterPlugin {
}
