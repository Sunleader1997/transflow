import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.sunyaxing.transflow.agent.agentplugin.DemoTest;
import org.sunyaxing.transflow.agent.agentplugin.MonitorInterceptor;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

public class BiteBuddyTest {
    public static void main(String[] args) throws IOException {
        Instrumentation instrumentation = ByteBuddyAgent.install();
//        new ByteBuddy()
//                .redefine(DemoTest.class)
//                .method(ElementMatchers.named("console"))
//                .intercept(MethodDelegation.to(MonitorInterceptor.class))
//                .make()
//                .load(DemoTest.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        new AgentBuilder.Default()
                .with(AgentBuilder.Listener.StreamWriting.toSystemError())
                .type(ElementMatchers.is(DemoTest.class))
                .transform((builder, typeDescription, classLoader, module) -> {
                    System.out.println("transform class " + typeDescription);
                    return builder.visit(Advice.to(MonitorInterceptor.class).on(ElementMatchers.named("console")));
                }).installOn(instrumentation);
        DemoTest demoTest = new DemoTest();
        System.out.println(demoTest.console("a"));
//        new AgentBuilder.Default()
//                .with(AgentBuilder.InitializationStrategy.Minimal.INSTANCE)
//                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
//                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
//                .type(ElementMatchers.is(DemoTest.class))
//                .transform((builder, typeDescription, classLoader, module) -> {
//                    System.out.println("transform class " + typeDescription);
//                    DynamicType.Builder<?> resBuilder = builder.method(ElementMatchers.isMethod())
//                            .intercept(MethodDelegation.to(MonitorInterceptor.class));
////                    try {
////                        DynamicType.Unloaded<?> unloaded = resBuilder.make();
////                        unloaded.load(classLoader, ClassReloadingStrategy.fromInstalledAgent())
////                                .saveIn(new File("D:\\workspace\\transflow\\agent\\agent-plugin\\src\\test\\java"));
////                        instrumentation.retransformClasses(DemoTest.class);
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
//                    return resBuilder;
//                }).installOnByteBuddyAgent();
//        DemoTest demoTest = new DemoTest();
//        demoTest.console("");
    }
}
