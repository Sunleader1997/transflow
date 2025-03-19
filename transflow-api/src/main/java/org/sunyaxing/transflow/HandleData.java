package org.sunyaxing.transflow;

import java.io.Serializable;
import java.util.List;

public class HandleData implements Serializable {
    // 数据来源于这个handle
    private String handleId;
    private List<TransData> transData;

    public HandleData(String handleId, List<TransData> transData) {
        this.handleId = handleId;
        this.transData = transData;
    }

    public List<TransData> getTransData() {
        return transData;
    }

    public void setTransData(List<TransData> transData) {
        this.transData = transData;
    }

    public String getHandleId() {
        return handleId;
    }

    public void setHandleId(String handleId) {
        this.handleId = handleId;
    }
}
