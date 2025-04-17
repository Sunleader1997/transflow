package org.sunyaxing.transflow.pluginjsonfilter;

import com.alibaba.fastjson2.JSONObject;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
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
public class JsonFilterExt extends TransFlowMiddleGatewayHandler<String> {
    private static final Logger log = LoggerFactory.getLogger(JsonFilterExt.class);

    public JsonFilterExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    protected void afterInitHandler(JSONObject config, List<Handle> handles) {

    }

    @Override
    public Function<TransData<String>, Boolean> parseHandleToConsumer(String handleId, String handleValue) {
        Expression expression = AviatorEvaluator.compile(handleValue);
        log.info("编译完成 \n {}", expression);
        return new AviatorScriptMiddleHandler(expression);
    }

    public static class AviatorScriptMiddleHandler implements Function<TransData<String>, Boolean> {

        private final Expression expression;

        public AviatorScriptMiddleHandler(Expression expression) {
            this.expression = expression;
        }

        @Override
        public Boolean apply(TransData<String> transData) {
            String jsonString = transData.getData();
            JSONObject jsonObject = JSONObject.parse(jsonString);
            JSONObject env = new JSONObject();
            env.put("data", jsonObject);
            Object result = expression.execute(env);
            // 保存修改后的信息
            transData.setData(jsonObject.toJSONString());
            boolean resultIsBool = result instanceof Boolean;
            return !resultIsBool || Boolean.TRUE.equals(result);
        }
    }

    @Override
    public void destroy() {
        log.info("jsonfilter destroy");
    }
}
