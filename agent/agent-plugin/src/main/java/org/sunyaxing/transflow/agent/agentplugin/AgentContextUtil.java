package org.sunyaxing.transflow.agent.agentplugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class AgentContextUtil {
    private static final Logger log = LoggerFactory.getLogger(AgentContextUtil.class);
    private static ThreadLocal<Stack<TraceData>> transDataStack = ThreadLocal.withInitial(Stack::new);
    private static ThreadLocal<Queue<TraceData>> queueForOut = ThreadLocal.withInitial(LinkedList::new);

    public static TraceData addTrace(String methodId) {
        int deep = transDataStack.get().size();
        TraceData traceData = new TraceData(methodId, deep);
        transDataStack.get().push(traceData);
        queueForOut.get().offer(traceData);
        return traceData;
    }

    public static TraceData popTrace() {
        TraceData traceData = transDataStack.get().pop();
        traceData.signEnd();
        if (transDataStack.get().isEmpty()) {
            TraceData queueData;
            while ((queueData = queueForOut.get().poll()) != null) {
                log.info("{}", queueData.toString());
            }
            transDataStack.remove();
            queueForOut.remove();
        }
        return traceData;
    }
}
