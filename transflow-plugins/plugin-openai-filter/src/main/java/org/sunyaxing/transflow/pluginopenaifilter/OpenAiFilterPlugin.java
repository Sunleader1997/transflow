package org.sunyaxing.transflow.pluginopenaifilter;

import org.sunyaxing.transflow.common.ano.HandleItem;
import org.sunyaxing.transflow.common.ano.JobParamItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;
import org.sunyaxing.transflow.plugins.TransFlowFilterPlugin;

@ScopeContentCheck(
        value = {
                @JobParamItem(field = "token", label = "token")
        },
        handle = @HandleItem(field = "prompt", label = "prompt", required = true, defaultValue = "")
)
public class OpenAiFilterPlugin extends TransFlowFilterPlugin {
}
