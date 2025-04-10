package org.sunyaxing.transflow.pluginjsongateway;

import com.alibaba.fastjson2.JSONObject;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.handlers.AbstractMiddleHandler;
import org.sunyaxing.transflow.extensions.DefaultMiddleExtensionWithHandler;
import org.sunyaxing.transflow.extensions.handlers.Handler;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.List;

@Extension
public class ScriptGatewayExt extends DefaultMiddleExtensionWithHandler {
    private static final Logger log = LoggerFactory.getLogger(ScriptGatewayExt.class);

    public ScriptGatewayExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    protected void afterInitHandler(JSONObject config, List<Handle> handles) {

    }

    @Override
    public Handler<TransData,Boolean> parseHandleToHandler(String handleId, String handleValue) {
        GroovyShell groovyShell = new GroovyShell();
        Script script = groovyShell.parse(handleValue);
        log.info("编译完成 \n {}", script);
        return new GatewayScriptMiddleHandler(script);
    }

    @Override
    public void destroy() {

    }

    public static class GatewayScriptMiddleHandler extends AbstractMiddleHandler {
        private final Script script;

        public GatewayScriptMiddleHandler(Script script) {
            this.script = script;
        }

        @Override
        public Boolean resolve(TransData transData) {
            JSONObject jsonObject = transData.getData(JSONObject.class);
            script.setProperty("data", jsonObject);
            Object o = script.run();
            return o instanceof Boolean && Boolean.TRUE.equals(o);
        }
    }
}
