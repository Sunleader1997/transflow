package org.sunyaxing.transflow.pluginjsonfilter;

import com.alibaba.fastjson2.JSONObject;
import org.pf4j.Extension;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.TransFlowFilter;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.Properties;

@Extension
public class JsonFilterExt extends TransFlowFilter<String, JSONObject> {
    public JsonFilterExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public TransData<JSONObject> doFilter(TransData<String> input) {
        return new TransData<>(input.offset(), JSONObject.parseObject(input.data()));
    }


    @Override
    public void init(Properties config) {

    }

    @Override
    public void destroy() {

    }
}
