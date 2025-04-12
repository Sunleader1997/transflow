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
public class DemoInputWithHandlerExt extends TransFlowInput<String, String> {
    private static final Logger log = LogManager.getLogger(DemoInputWithHandlerExt.class);
    private Thread inputDemoThead;

    public DemoInputWithHandlerExt(ExtensionContext extensionContext) {
        super(extensionContext);
        log.info("create");
    }

    @Override
    public void commit(HandleData<String> handleData) {

    }

    @Override
    protected void afterInitHandler(JSONObject config, List<Handle> handles) {
        inputDemoThead.start();
        this.inputDemoThead = new Thread(() -> {
            this.handlerMap.forEach((handlerId, handler) -> {
                handler.apply(null);
            });
        });
    }

    @Override
    public Function<TransData<String>, String> parseHandleToConsumer(String handleId, String handleValue) {
        return data->{

            return null;
        };
    }

    @Override
    public void destroy() {
        log.info("销毁生产线程");
        this.inputDemoThead.interrupt();
    }

}