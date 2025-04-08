package org.sunyaxing.transflow.agent.agentplugin;

import javassist.ClassPool;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

public class TransflowAgent {
    private static final Logger log = LoggerFactory.getLogger(TransflowAgent.class);
    private static PrintStream ps = System.err;

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        ClassPool pool = ClassPool.getDefault();
        Instrumentation agent = ByteBuddyAgent.install();
        for (Class<?> tclass : instrumentation.getAllLoadedClasses()) {
            try {
                if (AnnotationUtils.findAnnotation(tclass, RestController.class) != null) {
                    DynamicType.Builder<?> dynamicType = new ByteBuddy()
                            .redefine(tclass)
                            .visit(Advice.to(ControllerAdvice.class).on(ElementMatchers.isAnnotatedWith(GetMapping.class)));
                    byte[] bytes = dynamicType.make().getBytes();
                    instrumentation.redefineClasses(new ClassDefinition(tclass, bytes));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    static class DefineTransformer implements ClassFileTransformer {
//
//        @Override
//        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
//            if(className.startsWith("org/sunyaxing")){
//                System.out.println("premain load Class:" + className);
//            }
//            return classfileBuffer;
//
//        }
//    }
}
