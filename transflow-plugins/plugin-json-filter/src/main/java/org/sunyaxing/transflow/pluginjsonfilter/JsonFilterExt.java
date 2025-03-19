package org.sunyaxing.transflow.pluginjsonfilter;

import com.alibaba.fastjson2.JSONObject;
import com.googlecode.aviator.AviatorEvaluator;
import org.pf4j.Extension;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.TransFlowFilter;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Extension
public class JsonFilterExt extends TransFlowFilter {
    private static final Logger log = LoggerFactory.getLogger(JsonFilterExt.class);
    private String script;
    private final AtomicLong rec = new AtomicLong(0);
    private final AtomicLong send = new AtomicLong(0);

    public JsonFilterExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public Long getRecNumb() {
        return rec.get();
    }

    @Override
    public Long getSendNumb() {
        return send.get();
    }

    @Override
    public Long getRemainingDataSize() {
        return super.getRemainingDataSize();
    }

    @Override
    protected void initSelf(JSONObject config, List<Handle> handles) {

    }

    @Override
    public List<TransData> execDatas(String handleValue, List<TransData> input) {
        rec.addAndGet(input.size());
        List<TransData> sendData = input.stream()
                .filter(transData -> {
                    if (StringUtils.isNotNullOrEmpty(handleValue)) {
                        try {
                            JSONObject jsonObject = transData.getData(JSONObject.class);
                            Object o = AviatorEvaluator.execute(handleValue, jsonObject);
                            if (o instanceof Boolean) {
                                return Boolean.TRUE.equals(o);
                            } else {
                                transData.setData(jsonObject);
                                return true;
                            }
                        } catch (Exception e) {
                            log.error("脚本执行异常", e);
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
        send.addAndGet(sendData.size());
        return sendData;
    }



    @Override
    public void destroy() {
        log.info("jsonfilter destroy");
    }
}
