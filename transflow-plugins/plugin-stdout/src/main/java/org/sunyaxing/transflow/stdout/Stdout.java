package org.sunyaxing.transflow.stdout;

import com.alibaba.fastjson2.JSONObject;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.typesimpl.TransFlowOutputWithHandler;

import java.util.List;
import java.util.function.Function;

@Extension
public class Stdout extends TransFlowOutputWithHandler<String, String> {

    private final Logger logger = LoggerFactory.getLogger(Stdout.class);

    public Stdout(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public void afterInitHandler(JSONObject config, List<Handle> handles) {
        logger.info("初始化连接");
    }

    @Override
    public Function<TransData<String>, String> parseHandleToConsumer(String handleId, String handleValue) {
        return transData -> {
            logger.info("接收到数据，等待处理 {}", transData.getData());
            return transData.getData();
        };
    }

    @Override
    public void destroy() {
        logger.info("销毁连接");
    }

    @Override
    protected void batchExec(List<String> dataList) {

    }
}
