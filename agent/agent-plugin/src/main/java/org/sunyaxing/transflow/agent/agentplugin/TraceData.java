package org.sunyaxing.transflow.agent.agentplugin;

import java.util.Collections;

public class TraceData {
    private final String methodId;
    private final Integer deep;
    private final Long startTime;
    private Long endTime;

    public TraceData(String methodId, Integer deep) {
        this.methodId = methodId;
        this.deep = deep;
        this.startTime = System.currentTimeMillis();
        this.endTime = System.currentTimeMillis();
    }

    public void signEnd() {
        this.endTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        String deepStr = String.join("", Collections.nCopies(deep, "├——"));
        return deepStr + methodId + "\t\t[" + (endTime - startTime) + "ms]";
    }
}
