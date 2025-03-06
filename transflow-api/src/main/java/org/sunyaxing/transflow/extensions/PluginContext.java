package org.sunyaxing.transflow.extensions;

import org.pf4j.RuntimeMode;

/**
 * 创建插件时需要的参数
 */
public class PluginContext {
    private final RuntimeMode runtimeMode;

    public PluginContext(RuntimeMode runtimeMode) {
        this.runtimeMode = runtimeMode;
    }

    public RuntimeMode getRuntimeMode() {
        return runtimeMode;
    }
}
