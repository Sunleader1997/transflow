package org.sunyaxing.transflow;

import java.io.Serializable;

public class HandleData implements Serializable {
    // 数据来源于这个handle
    private String handleId;
    private TransData transData;

    public HandleData(String handleId, TransData transData) {
        this.handleId = handleId;
        this.transData = transData;
    }

    public TransData getTransData() {
        return transData;
    }

    public void setTransData(TransData transData) {
        this.transData = transData;
    }

    public String getHandleId() {
        return handleId;
    }

    public void setHandleId(String handleId) {
        this.handleId = handleId;
    }
}
