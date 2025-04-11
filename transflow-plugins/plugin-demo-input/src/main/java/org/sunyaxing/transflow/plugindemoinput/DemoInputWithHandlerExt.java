package org.sunyaxing.transflow.plugindemoinput;

import com.alibaba.fastjson2.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pf4j.Extension;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.types.TransFlowInput;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Extension
public class DemoInputWithHandlerExt extends TransFlowInput<String, String, HandleData<String>> {
    private static final Logger log = LogManager.getLogger(DemoInputWithHandlerExt.class);
    private final Thread inputDemoThead;

    public DemoInputWithHandlerExt(ExtensionContext extensionContext) {
        super(extensionContext);
        log.info("create");
        this.inputDemoThead = new Thread(() -> {
            this.handlerMap.forEach((handlerId, handler) -> {
                putQueueLast(handler.apply(null));
            });
        });
    }

    @Override
    protected void afterInitHandler(JSONObject config, List<Handle> handles) {
        inputDemoThead.start();
    }

    @Override
    public Function<TransData<String>, HandleData<String>> parseHandleToConsumer(String handleId, String handleValue) {
        return transData -> new HandleData<>(handleId, new TransData<>(0L, handleValue));
    }


    @Override
    public Optional<HandleData<String>> exec(HandleData<String> handleData) {
        return Optional.of(handleData);
    }

    @Override
    public void commit(HandleData<String> offset) {
//        log.info("提交偏移量 {}", offset);
    }

    @Override
    protected HandleData<String> parseRToHandleData(HandleData<String> data) {
        return data;
    }

    @Override
    public void destroy() {
        log.info("销毁生产线程");
        this.inputDemoThead.interrupt();
    }

}