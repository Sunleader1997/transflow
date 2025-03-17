package org.sunyaxing.transflow.plugindemoinput;

import com.alibaba.fastjson2.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pf4j.Extension;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;


@Extension
public class DemoInputExt extends TransFlowInput {
    private static final Logger log = LogManager.getLogger(DemoInputExt.class);
    private String jsonStr;
    private final AtomicLong rec = new AtomicLong(0);
    private final BlockingDeque<String> queue = new LinkedBlockingDeque<>(100);
    public DemoInputExt(ExtensionContext extensionContext) {
        super(extensionContext);
        log.info("create");
    }

    @Override
    public void commit(Long offset) {
//        log.info("提交偏移量 {}", offset);
    }

    @Override
    public Long getRemainingDataSize() {
        return this.queue.stream().count();
    }

    @Override
    public Long getRecNumb() {
        return rec.get();
    }

    @Override
    public Long getSendNumb() {
        return rec.get();
    }


    @Override
    public List<TransData> dequeue() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String queue = this.queue.poll();
        List<TransData> transData = new ArrayList<>();
        transData.add(new TransData(0L, queue));
        rec.incrementAndGet();
        return transData;
    }

    @Override
    public void run() {
    }

    @Override
    public void init(JSONObject config) {
        this.jsonStr = config.getString("jsonStr");
        for(int i = 0; i < 100; i++){
            this.queue.add(this.jsonStr);
        }
    }

    @Override
    public void destroy() {
    }
}