package org.sunyaxing.transflow.stdout;

import com.alibaba.fastjson2.JSONObject;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.TransFlowOutput;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Extension
public class Stdout extends TransFlowOutput {
    private Logger logger = LoggerFactory.getLogger(Stdout.class);
    private final AtomicLong rec = new AtomicLong(0);
    public Stdout(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public Long getRecNumb() {
        return rec.get();
    }

    @Override
    public List<TransData> execDatas(String handleValue, List<TransData> data) {
        rec.addAndGet(data.size());
        logger.info("输出 {} 数据：{}", handleValue, data.size());
        return data;
    }

    @Override
    public void initSelf(JSONObject config, List<Handle> handles) {
        logger.info("初始化连接");
    }

    @Override
    public void destroy() {
        logger.info("销毁连接");
    }
}
