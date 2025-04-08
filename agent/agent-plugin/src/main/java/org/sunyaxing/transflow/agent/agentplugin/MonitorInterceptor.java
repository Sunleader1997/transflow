package org.sunyaxing.transflow.agent.agentplugin;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class MonitorInterceptor {
    private static final Logger log = LoggerFactory.getLogger(MonitorInterceptor.class);

    @RuntimeType
    public static Object intercept(@Origin Method method,
                                   @SuperCall Callable<?> callable) throws Exception {
        long start = System.currentTimeMillis();
        try {
            return callable.call();
        } finally {
            long duration = System.currentTimeMillis() - start;
            log.info("------------{}数据耗时---------- {}", method, duration);
        }
    }
}