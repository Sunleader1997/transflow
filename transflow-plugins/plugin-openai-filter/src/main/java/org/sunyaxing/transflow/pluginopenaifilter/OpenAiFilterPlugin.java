package org.sunyaxing.transflow.pluginopenaifilter;

import org.sunyaxing.transflow.common.ano.HandleItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;
import org.sunyaxing.transflow.plugins.TransFlowFilterPlugin;

@ScopeContentCheck(
        handle = @HandleItem(field = "config", label = "脚本", required = true, type = "code", defaultValue = "")
)
public class OpenAiFilterPlugin extends TransFlowFilterPlugin {
}
