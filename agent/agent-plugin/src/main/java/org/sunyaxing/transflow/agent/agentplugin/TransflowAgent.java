package org.sunyaxing.transflow.agent.agentplugin;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.PrintStream;
import java.lang.instrument.Instrumentation;

public class TransflowAgent {
    private static final Logger log = LoggerFactory.getLogger(TransflowAgent.class);
    private static PrintStream ps = System.out;

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {

        new AgentBuilder.Default()
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
//                .type(
//                        ElementMatchers.isAnnotatedWith(RestController.class)
//                )
                .type(
                        ElementMatchers.nameStartsWith("org.sunyaxing")
                                .and(ElementMatchers.not(ElementMatchers.isInterface()))
                )
                .transform((builder, typeDescription, classLoader, module) -> {
                    log.info("transform " + typeDescription);
                    DynamicType.Builder<?> dynamicType = builder
//                            .visit(Advice.to(ControllerAdvice.class).on(ElementMatchers.isAnnotatedWith(GetMapping.class)));
                            .visit(Advice.to(ControllerAdvice.class).on(
                                    ElementMatchers.isMethod()
                                            .and(ElementMatchers.not(ElementMatchers.isConstructor()))
                                            .and(ElementMatchers.not(ElementMatchers.isStatic()))
                                            .and(ElementMatchers.not(ElementMatchers.nameStartsWith("get")))
                                            .and(ElementMatchers.not(ElementMatchers.nameStartsWith("set")))
                            ));
                    return dynamicType;
                }).installOn(instrumentation);
    }
}
