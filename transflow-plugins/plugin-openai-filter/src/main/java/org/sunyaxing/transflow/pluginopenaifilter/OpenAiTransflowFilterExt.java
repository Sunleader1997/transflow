package org.sunyaxing.transflow.pluginopenaifilter;

import com.alibaba.fastjson2.JSONObject;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import org.pf4j.Extension;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.DefaultMiddleExtensionWithHandler;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.handlers.Handler;

import java.util.List;

@Extension
public class OpenAiTransflowFilterExt extends DefaultMiddleExtensionWithHandler {

    public OpenAiTransflowFilterExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    protected void afterInitHandler(JSONObject config, List<Handle> handles) {
        String token = config.getString("token");
        DeepSeekClient deepSeekClient = new DeepSeekClient.Builder()
                .model("deepseek-reasoner")
                .baseUrl("https://api.deepseek.com")
                .build();
    }

    @Override
    public Handler<TransData, Boolean> parseHandleToHandler(String handleId, String handle) {
    }

    @Override
    public void destroy() {

    }
}
