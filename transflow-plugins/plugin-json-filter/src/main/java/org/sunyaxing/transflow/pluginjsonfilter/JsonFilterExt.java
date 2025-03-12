package org.sunyaxing.transflow.pluginjsonfilter;

import com.alibaba.fastjson2.JSONObject;
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

@Extension
public class JsonFilterExt extends TransFlowFilter {
    private static final Logger log = LoggerFactory.getLogger(JsonFilterExt.class);
    private ScriptEngine scriptEngine;
    private String script;

    public JsonFilterExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public List<TransData> doFilter(List<TransData> input) {
        return input.stream()
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
                            log.error("groovy 脚本执行异常");
                            return false;
                        }
                    }
                    return true;
                })
                .toList();
    }


    @Override
    public void init(Properties config) {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.scriptEngine = manager.getEngineByName("groovy");
        this.script = config.getProperty("script");
    }

    @Override
    public void destroy() {

    }
}
