package org.sunyaxing.transflow.agent.agentplugin;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class ControllerAdvice {

    @Advice.OnMethodEnter
    public static void enter(@Advice.Origin Method method, @Advice.This Object thiz) {
        System.out.println("注入代码 >> " + method);
    }
}