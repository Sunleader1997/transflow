package org.sunyaxing.transflow.pluginjsonfilter;

import com.alibaba.fastjson2.JSONObject;
import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory;
import org.pf4j.Extension;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.TransFlowFilter;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

@Extension
public class JsonFilterExt extends TransFlowFilter {
    private static final Logger log = LoggerFactory.getLogger(JsonFilterExt.class);
    private ScriptEngine scriptEngine;
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
    public List<TransData> execDatas(String handle, List<TransData> input) {
        rec.addAndGet(input.size());
        List<TransData> sendData =  input.stream()
                .map(result -> {
                    if (result.isType(JSONObject.class)) {
                        return result;
                    } else {
                        return new TransData(result.offset(), JSONObject.parseObject(JSONObject.toJSONString(result.data())));
                    }
                })
                .filter(transData -> {
                    if (StringUtils.isNotNullOrEmpty(this.script)) {
                        try {
                            scriptEngine.put("data", transData.data());
                            Object res = scriptEngine.eval(this.script);
                            return Boolean.TRUE.equals(res);
                        } catch (Exception e) {
                            log.error("groovy 脚本执行异常", e);
                            return false;
                        }
                    }
                    return true;
                })
                .toList();
        send.addAndGet(sendData.size());
        return sendData;
    }


    @Override
    public void init(JSONObject config) {
        ScriptEngineManager manager = new ScriptEngineManager();
        manager.registerEngineName("groovy", new GroovyScriptEngineFactory());
        this.scriptEngine = manager.getEngineByName("groovy");
        this.script = config.getString("script");
    }

    @Override
    public void destroy() {

    }
}
