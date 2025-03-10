package org.sunyaxing.transflow.stdout;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.extensions.TransFlowOutput;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.Map;
import java.util.Properties;

@Extension
public class Stdout extends TransFlowOutput {
    private Logger logger = LoggerFactory.getLogger(Stdout.class);

    public Stdout(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public void output(Object data) {
        logger.info("输出数据：{}", data);
    }

    @Override
    public void init(Properties config) {
        logger.info("初始化连接");
    }

    @Override
    public void destroy() {
        logger.info("销毁连接");
    }
}
