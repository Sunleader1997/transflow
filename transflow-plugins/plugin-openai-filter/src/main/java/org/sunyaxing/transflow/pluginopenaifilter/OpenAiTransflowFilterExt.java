package org.sunyaxing.transflow.pluginopenaifilter;

import com.alibaba.fastjson2.JSONObject;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionModel;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest;
import io.github.pigmesh.ai.deepseek.core.chat.ResponseFormatType;
import org.pf4j.Extension;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.base.typesimpl.TransFlowMiddleGatewayHandler;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.List;
import java.util.function.Function;

@Extension
public class OpenAiTransflowFilterExt extends TransFlowMiddleGatewayHandler {
    private DeepSeekClient deepSeekClient;

    public OpenAiTransflowFilterExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    protected void afterInitHandler(JSONObject config, List<Handle> handles) {
        String token = config.getString("token");
        this.deepSeekClient = new DeepSeekClient.Builder()
                .openAiApiKey(token)
                .baseUrl("https://api.deepseek.com")
                .build();
    }

    @Override
    public Function<TransData, Boolean> parseHandleToConsumer(String handleId, String prompt) {
        return transData -> {
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    // 模型选择，支持 DEEPSEEK_CHAT、DEEPSEEK_REASONER 等
                    .model(ChatCompletionModel.DEEPSEEK_REASONER)
                    .addSystemMessage(prompt)
                    .addUserMessage(transData.getData(String.class))
                    .maxTokens(1000)
                    .responseFormat(ResponseFormatType.JSON_OBJECT)
                    //.tools(...) // 可选
                    .build();
            String res = this.deepSeekClient.chatCompletion(request).execute().content();
            transData.setData(res);
            return true;
        };
    }

    @Override
    public void destroy() {
        this.deepSeekClient.shutdown();
    }
}
