package org.sunyaxing.transflow;

import java.io.Serializable;


public class TransData<T> implements Serializable {
    private Long offset;
    private T data;

    public TransData(Long offset, T data) {
        this.offset = offset;
        this.data = data;
    }

//    public <T> T getData(Class<T> clazz) {
//        if (clazz.isInstance(data)) {
//            return clazz.cast(data);
//        }else{
//            T resData = null;
//            if (String.class.equals(clazz)) {
//                resData = clazz.cast(data.toString());
//            } else if (JSONObject.class.equals(clazz)) {
//                if (data instanceof String) {
//                    resData = clazz.cast(JSONObject.parseObject((String) data));
//                } else {
//                    resData = clazz.cast(JSONObject.parseObject(JSONObject.toJSONString(data)));
//                }
//            }
//            return resData;
//        }
//    }

    public boolean isType(Class<?> tClass) {
        return tClass.isInstance(data);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }
}
