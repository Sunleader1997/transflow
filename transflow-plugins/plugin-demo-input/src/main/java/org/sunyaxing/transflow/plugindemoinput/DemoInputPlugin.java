package org.sunyaxing.transflow.plugindemoinput;

import org.sunyaxing.transflow.common.ano.JobParamItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;
import org.sunyaxing.transflow.plugins.TransFlowInputPlugin;


@ScopeContentCheck(
        @JobParamItem(field = "jsonStr", label = "jsonStr", required = true, type = String.class)
)
public class DemoInputPlugin extends TransFlowInputPlugin {
}
