package org.sunyaxing.transflow.pluginjsongateway;

import com.alibaba.fastjson2.JSONObject;
import com.googlecode.aviator.AviatorEvaluator;
import javafx.util.Pair;
import org.pf4j.Extension;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.TransFlowGateway;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Extension
public class JsonGatewayExt extends TransFlowGateway {
    private static final Logger log = LoggerFactory.getLogger(JsonGatewayExt.class);

    public JsonGatewayExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    protected Pair<List<TransData>, List<TransData>> execDatasWithPair(String handleValue, List<TransData> input) {
        List<TransData> result = new ArrayList<>();
        List<TransData> remain = new ArrayList<>();

        for (TransData transData : input) {
            if (StringUtils.isNotNullOrEmpty(handleValue)) {
                try {
                    JSONObject jsonObject = transData.getData(JSONObject.class);
                    Object o = AviatorEvaluator.execute(handleValue, jsonObject);
                    if (o instanceof Boolean && Boolean.TRUE.equals(o)) {
                        result.add(transData);
                    } else {
                        remain.add(transData);
                    }
                } catch (Exception e) {
                    log.error("脚本执行异常", e);
                    remain.add(transData);
                }
            }else {
                result.add(transData);
            }
        }

        return new Pair<>(result, remain);
    }

    @Override
    protected void initSelf(JSONObject config, List<Handle> handles) {

    }

    @Override
    protected List<TransData> execDatas(String handleValue, List<TransData> data) {
        return Collections.emptyList();
    }

    @Override
    public void destroy() {

    }
}
