package org.sunyaxing.transflow;

import com.alibaba.fastjson2.JSONObject;

import java.io.Serializable;


public record TransData(Long offset, Object data) implements Serializable {
    public <T> T getData(Class<T> clazz) {
        if(clazz.isInstance(data)){
            return clazz.cast(data);
        }
        if (String.class.equals(clazz)) {
            return clazz.cast(data.toString());
        } else if (JSONObject.class.equals(clazz)) {
            if (data instanceof String) {
                return clazz.cast(JSONObject.parseObject((String) data));
            }else{
                return clazz.cast(JSONObject.parseObject(JSONObject.toJSONString(data)));
            }
        }
        return clazz.cast(data);
    }
    public boolean isType(Class<?> tClass){
        return tClass.isInstance(data);
    }
}
