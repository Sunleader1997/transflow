package org.sunyaxing.transflow.pluginjsongateway;

import com.alibaba.fastjson2.JSONObject;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
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
import java.util.HashMap;
import java.util.List;

@Extension
public class JsonGatewayExt extends TransFlowGateway {
    private static final Logger log = LoggerFactory.getLogger(JsonGatewayExt.class);
    private HashMap<String, Script> scriptHashMap;

    public JsonGatewayExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    /**
     * 初始化执行脚本
     *
     * @param config
     * @param handles
     */
    @Override
    protected void initSelf(JSONObject config, List<Handle> handles) {
        this.scriptHashMap = new HashMap<>();
        handles.forEach(handle -> {
            String id = handle.getId();
            GroovyShell groovyShell = new GroovyShell();
            Script script = groovyShell.parse(handle.getValue());
            scriptHashMap.put(id, script);
            log.info("编译完成 \n {}", script);
        });
    }

    @Override
    protected Pair<List<TransData>, List<TransData>> execDatasWithPair(String handleId, String handleValue, List<TransData> input) {
        List<TransData> result = new ArrayList<>();
        List<TransData> remain = new ArrayList<>();

        for (TransData transData : input) {
            if (StringUtils.isNotNullOrEmpty(handleValue)) {
                try {
                    JSONObject jsonObject = transData.getData(JSONObject.class);
                    Script script = this.scriptHashMap.get(handleId);
                    script.setProperty("data", jsonObject);
                    Object o = script.run();
                    if (o instanceof Boolean && Boolean.TRUE.equals(o)) {
                        result.add(transData);
                    } else {
                        remain.add(transData);
                    }
                } catch (Exception e) {
                    log.error("脚本执行异常", e);
                    remain.add(transData);
                }
            } else {
                result.add(transData);
            }
        }

        return new Pair<>(result, remain);
    }

    @Override
    protected List<TransData> execDatas(String handleValue, List<TransData> data) {
        return Collections.emptyList();
    }

    @Override
    public void destroy() {

    }
}
