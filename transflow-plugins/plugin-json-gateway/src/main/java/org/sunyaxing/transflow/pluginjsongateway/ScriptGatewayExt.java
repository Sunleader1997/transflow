package org.sunyaxing.transflow.pluginjsongateway;

import com.alibaba.fastjson2.JSONObject;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.typesimpl.TransFlowMiddleGatewayHandler;

import java.util.List;
import java.util.function.Function;

@Extension
public class ScriptGatewayExt extends TransFlowMiddleGatewayHandler<String> {
    private static final Logger log = LoggerFactory.getLogger(ScriptGatewayExt.class);

    public ScriptGatewayExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    protected void afterInitHandler(JSONObject config, List<Handle> handles) {

    }

    @Override
    public Function<TransData<String>, Boolean> parseHandleToConsumer(String handleId, String handleValue) {
        GroovyShell groovyShell = new GroovyShell();
        Script script = groovyShell.parse(handleValue);
        log.info("编译完成 \n {}", script);
        return new GatewayScriptMiddleHandler(script);
    }


    @Override
    public void destroy() {

    }

    public static class GatewayScriptMiddleHandler implements Function<TransData<String>, Boolean> {
        private final Script script;

        public GatewayScriptMiddleHandler(Script script) {
            this.script = script;
        }

        @Override
        public Boolean apply(TransData<String> transData) {
            String jsonString = transData.getData();
            JSONObject jsonObject = JSONObject.parse(jsonString);
            script.setProperty("data", jsonObject);
            Object o = script.run();
            // 保存修改后的信息
            transData.setData(jsonObject.toJSONString());
            return o instanceof Boolean && Boolean.TRUE.equals(o);
        }
    }
}
