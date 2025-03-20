package org.sunyaxing.transflow.plugindemoinput;

import com.alibaba.fastjson2.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pf4j.Extension;
import org.pf4j.util.StringUtils;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.TransFlowMultiInput;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;


@Extension
public class DemoInputExt extends TransFlowMultiInput {
    private static final Logger log = LogManager.getLogger(DemoInputExt.class);
    private String jsonStr;
    private final AtomicLong rec = new AtomicLong(0);
    private final BlockingDeque<String> queue = new LinkedBlockingDeque<>(10000);

    public DemoInputExt(ExtensionContext extensionContext) {
        super(extensionContext);
        log.info("create");
    }

    @Override
    public List<HandleData> handleDequeue() {
        List<HandleData> res = new ArrayList<>();
        this.handleMap.forEach((k, v) -> {
            List<TransData> transData = new ArrayList<>();
            transData.add(new TransData(0L, v));
            res.add(new HandleData(k, transData));
            rec.incrementAndGet();
        });
        return res;
    }

    @Override
    protected void initSelf(JSONObject config, List<Handle> handles) {
    }

    @Override
    public void commit(HandleData offset) {
//        log.info("提交偏移量 {}", offset);
    }

    @Override
    public HandleData dequeue() {
        String queue = this.queue.poll();
        if (!StringUtils.isNullOrEmpty(queue)) {
            List<TransData> transData = new ArrayList<>();
            transData.add(new TransData(0L, queue));
            rec.incrementAndGet();
            return new HandleData("", transData);
        }
        return null;
    }


    @Override
    public void destroy() {
        log.info("demoInput destroy");
    }
}