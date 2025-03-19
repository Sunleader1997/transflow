package org.sunyaxing.transflow.transflowapp.common;

public interface Countable {
    public default Long getRecNumb() {
        return 0L;
    }

    public default Long getSendNumb() {
        return 0L;
    }

    // 获取剩余未消费的数据量
    public default Long getRemainingDataSize() {
        return 0L;
    }
}
