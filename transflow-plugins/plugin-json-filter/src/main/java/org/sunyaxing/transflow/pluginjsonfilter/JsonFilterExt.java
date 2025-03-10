package org.sunyaxing.transflow.pluginjsonfilter;

import com.alibaba.fastjson2.JSONObject;
import org.pf4j.Extension;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.TransFlowFilter;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.List;
import java.util.Properties;

@Extension
public class JsonFilterExt extends TransFlowFilter {
    public JsonFilterExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public List<TransData> doFilter(List<TransData> input) {
        return input.stream()
                .map(result -> new TransData(result.offset(), JSONObject.parseObject(JSONObject.toJSONString(result.data()))))
                .toList();
    }


    @Override
    public void init(Properties config) {

    }

    @Override
    public void destroy() {

    }
}
