package org.sunyaxing.transflow;

import java.io.Serializable;

public class HandleData<T> implements Serializable {
    // 数据来源于这个handle
    private String handleId;
    private TransData<T> transData;

    public HandleData(String handleId, TransData<T> transData) {
        this.handleId = handleId;
        this.transData = transData;
    }

    public TransData<T> getTransData() {
        return transData;
    }

    public void setTransData(TransData<T> transData) {
        this.transData = transData;
    }

    public String getHandleId() {
        return handleId;
    }

    public void setHandleId(String handleId) {
        this.handleId = handleId;
    }
}
