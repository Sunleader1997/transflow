package org.sunyaxing.transflow.agent.agentplugin;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;
import java.util.UUID;

public class ControllerAdvice {

    @Advice.OnMethodEnter
    public static TraceData enter(@Advice.Origin("#t") String className, @Advice.Origin Method method, @Advice.This Object thiz) {
        TraceData traceData = AgentContextUtil.addTrace(className + "." + method.getName());
        String linkId = TrackManager.getCurrentSpan();
        if (null == linkId) {
            linkId = UUID.randomUUID().toString();
            TrackContext.setLinkId(linkId);
        }
        TrackManager.createEntrySpan();
        return traceData;
    }

    @Advice.OnMethodExit
    public static void exit(@Advice.Enter TraceData traceData, @Advice.Origin("#t") String className, @Advice.Origin("#m") String methodName) {
        TraceData popTrance = AgentContextUtil.popTrace();
        if (popTrance != traceData) {
            System.out.println("异常");
        }
    }
}