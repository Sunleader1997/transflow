package org.sunyaxing.transflow.stdout;

import com.alibaba.fastjson2.JSONObject;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.TransFlowOutput;
import org.sunyaxing.transflow.extensions.TransFlowOutputWithHandler;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.handlers.DefaultStringHandler;
import org.sunyaxing.transflow.extensions.handlers.Handler;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Extension
public class Stdout extends TransFlowOutputWithHandler<List<TransData>> {
    private Logger logger = LoggerFactory.getLogger(Stdout.class);
    private final AtomicLong rec = new AtomicLong(0);

    public Stdout(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    protected void execData(List<TransData> data) {
        rec.addAndGet(data.size());
        logger.info("输出 数据：{}", JSONObject.toJSONString(data));
    }

    @Override
    public void afterInitHandler(JSONObject config, List<Handle> handles) {
        logger.info("初始化连接");
    }

    @Override
    public Handler<List<TransData>, List<TransData>> parseHandleToHandler(String handleId, String handleValue) {
        return new Handler<List<TransData>, List<TransData>>() {
            @Override
            public List<TransData> resolve(List<TransData> data) {
                logger.info("接收到数据，等待处理 {}", JSONObject.toJSONString(data));
                return data;
            }
        };
    }

    @Override
    public void destroy() {
        logger.info("销毁连接");
    }
}
