package org.sunyaxing.transflow.plugindemoinput;

import com.alibaba.fastjson2.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pf4j.Extension;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.TransFlowInputWithHandler;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.handlers.AbstractInputHandler;
import org.sunyaxing.transflow.extensions.handlers.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


@Extension
public class DemoInputWithHandlerExt extends TransFlowInputWithHandler<String, List<TransData>> {
    private static final Logger log = LogManager.getLogger(DemoInputWithHandlerExt.class);
    private final BlockingDeque<String> queue = new LinkedBlockingDeque<>(10000);

    public DemoInputWithHandlerExt(ExtensionContext extensionContext) {
        super(extensionContext);
        log.info("create");
    }

    @Override
    protected void afterInitHandler(JSONObject config, List<Handle> handles) {

    }

    @Override
    public Handler<String, List<TransData>> parseHandleToHandler(String handleId, String handleValue) {
        return new DemoInputHandler(handleValue);
    }

    @Override
    public List<HandleData> handleDequeue() {
        List<HandleData> res = new ArrayList<>();
        this.handlerMap.forEach((handlerId, handler) -> {
            res.add(new HandleData(handlerId, handler.resolve(null)));
        });
        return res;
    }

    @Override
    public void commit(HandleData offset) {
//        log.info("提交偏移量 {}", offset);
    }


    @Override
    public void destroy() {
        log.info("demoInput destroy");
    }

    public static class DemoInputHandler extends AbstractInputHandler<String> {

        private final String data;

        public DemoInputHandler(String data) {
            this.data = data;
        }

        @Override
        public List<TransData> resolve(String ignore) {
            List<TransData> transData = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                transData.add(new TransData(0L, data));
            }
            return transData;
        }
    }
}