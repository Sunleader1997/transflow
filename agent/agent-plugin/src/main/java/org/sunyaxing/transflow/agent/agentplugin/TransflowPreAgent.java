package org.sunyaxing.transflow.agent.agentplugin;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class TransflowPreAgent {
    private static final Logger log = LoggerFactory.getLogger(TransflowPreAgent.class);
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        new AgentBuilder.Default()
                //.with(AgentBuilder.Listener.StreamWriting.toSystemError())
                .type(ElementMatchers.isAnnotatedWith(RestController.class))
                .transform((builder, typeDescription, classLoader, module) -> {
                    System.out.println("transform class " + typeDescription);
                    DynamicType.Builder<?> dynamicType = builder
                            .method(ElementMatchers.isAnnotatedWith(GetMapping.class))
                            .intercept(MethodDelegation.to(MonitorInterceptor.class));
                    try{
                        dynamicType.make().saveIn(new File("D:\\workspace\\transflow\\agent\\agent-plugin\\src\\test\\java"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return dynamicType;
                }).installOn(instrumentation);
//        new AgentBuilder.Default()
//                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
////                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
//                .type(ElementMatchers.isAnnotatedWith(RestController.class))
//                .transform(new AgentBuilder.Transformer() {
//                    @Override
//                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule) {
//                        log.info("premain transform class {} {}",typeDescription,classLoader);
//                        return builder
//                                .visit(Advice.to(MonitorInterceptor.class).on(
//                                        ElementMatchers.isAnnotatedWith(RequestMapping.class)
//                                                .or(ElementMatchers.isAnnotatedWith(GetMapping.class))
//                                                .or(ElementMatchers.isAnnotatedWith(PostMapping.class))
//                                ));
//                    }
//                })
//                .installOn(instrumentation);
//        log.info("premain agent start");
    }
}
