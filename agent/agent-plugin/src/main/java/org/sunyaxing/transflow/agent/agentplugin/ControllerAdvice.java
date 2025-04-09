package org.sunyaxing.transflow.agent.agentplugin;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;
import java.util.UUID;

public class ControllerAdvice {
    @Advice.OnMethodEnter
    public static void enter(@Advice.Origin("#t") String className, @Advice.Origin Method method, @Advice.This Object thiz) {
        String linkId = TrackManager.getCurrentSpan();
        if (null == linkId) {
            linkId = UUID.randomUUID().toString();
            TrackContext.setLinkId(linkId);
        }
        TrackManager.createEntrySpan();
    }

    @Advice.OnMethodExit
    public static void exit(@Advice.Origin("#t") String className, @Advice.Origin("#m") String methodName) {
        TrackManager.getExitSpan();
        String info = String.format("\nspan: %s\t className: %s\t method: %s\t ",
                TrackContext.getLinkId(),
                className,
                methodName
        );
        System.out.println(info);
    }
}