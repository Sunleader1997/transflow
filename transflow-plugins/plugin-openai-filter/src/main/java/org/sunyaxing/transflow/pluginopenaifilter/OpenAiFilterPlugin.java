package org.sunyaxing.transflow.pluginopenaifilter;

import org.pf4j.Plugin;
import org.sunyaxing.transflow.common.ano.HandleItem;
import org.sunyaxing.transflow.common.ano.JobParamItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;

@ScopeContentCheck(
        value = {
                @JobParamItem(field = "token", label = "token")
        },
        handle = @HandleItem(field = "prompt", label = "prompt", required = true, defaultValue = "")
)
public class OpenAiFilterPlugin extends Plugin {
}
