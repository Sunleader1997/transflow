package org.sunyaxing.transflow;

import com.alibaba.fastjson2.JSONObject;

import java.io.Serializable;


public class TransData implements Serializable {
    private Long offset;
    private Object data;

    public TransData(Long offset, Object data) {
        this.offset = offset;
        this.data = data;
    }

    public <T> T getData(Class<T> clazz) {
        if (clazz.isInstance(data)) {
            return clazz.cast(data);
        }
        if (String.class.equals(clazz)) {
            return clazz.cast(data.toString());
        } else if (JSONObject.class.equals(clazz)) {
            if (data instanceof String) {
                return clazz.cast(JSONObject.parseObject((String) data));
            } else {
                return clazz.cast(JSONObject.parseObject(JSONObject.toJSONString(data)));
            }
        }
        return clazz.cast(data);
    }

    public boolean isType(Class<?> tClass) {
        return tClass.isInstance(data);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }
}
