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
        // 如果对象是这个类型，就直接返回原来的对象
        if (clazz.isInstance(data)) {
            return clazz.cast(data);
        }else{// 如果对象不是这个类型，就进行类型转换，并替换掉原有的对象
            T resData = null;
            if (String.class.equals(clazz)) {
                resData = clazz.cast(data.toString());
            } else if (JSONObject.class.equals(clazz)) {
                if (data instanceof String) {
                    resData = clazz.cast(JSONObject.parseObject((String) data));
                } else {
                    resData = clazz.cast(JSONObject.parseObject(JSONObject.toJSONString(data)));
                }
            }
            this.setData(resData);
            return resData;
        }
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
