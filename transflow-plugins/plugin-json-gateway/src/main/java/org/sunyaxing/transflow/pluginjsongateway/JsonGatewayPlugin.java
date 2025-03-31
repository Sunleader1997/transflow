package org.sunyaxing.transflow.pluginjsongateway;

import org.sunyaxing.transflow.common.ano.HandleItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;
import org.sunyaxing.transflow.plugins.TransFlowGatewayPlugin;

@ScopeContentCheck(
        handle = @HandleItem(field = "config", label = "脚本", required = true, type = "code", defaultValue = "")
)
public class JsonGatewayPlugin extends TransFlowGatewayPlugin {
}
