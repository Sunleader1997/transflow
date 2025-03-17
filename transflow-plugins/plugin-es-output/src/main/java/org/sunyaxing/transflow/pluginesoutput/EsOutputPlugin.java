package org.sunyaxing.transflow.pluginesoutput;

import org.sunyaxing.transflow.common.ano.JobParamItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;
import org.sunyaxing.transflow.plugins.TransFlowOutputPlugin;

@ScopeContentCheck({
        @JobParamItem(field = "index-name", label = "索引名称", defaultValue = "index-transflow"),
        @JobParamItem(field = "host", label = "IP", defaultValue = "127.0.0.1"),
        @JobParamItem(field = "port", label = "PORT", defaultValue = "9200"),
        @JobParamItem(field = "username", label = "用户名", defaultValue = "elastic"),
        @JobParamItem(field = "password", label = "密码", type = "password")
})
public class EsOutputPlugin extends TransFlowOutputPlugin {
}
