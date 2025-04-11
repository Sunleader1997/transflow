package org.sunyaxing.transflow.extensions;

import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

import java.util.Optional;
import java.util.function.Function;

/**
 * 中间扩展点
 * 同时拥有输入输出
 * 子类需要实现 handler 的初始化以按自己的方式处理/过滤单个数据
 */
public abstract class DefaultMiddleExtensionWithHandler extends ExtensionLifecycle<TransData, Boolean> {
    private static final Logger log = LoggerFactory.getLogger(DefaultMiddleExtensionWithHandler.class);


    public DefaultMiddleExtensionWithHandler(ExtensionContext extensionContext) {
        super(extensionContext);
    }


    /**
     * 接收处理上一个节点的数据
     * 默认情况下
     * 每个节点接收到数据之后，根据 execDatasWithPair 的处理结果
     * 将匹配到的数据分发给下一个节点
     * 将未匹配的结果交给下一个处理器
     *
     * @param handleData             上一个节点汇入的数据
     * @param #{handleData.handleId} 指定当前数据由哪个处理器处理
     * @return 返回给下一个节点的数据, 已经根据处理器做好分类，表明数据是由哪个处理器处理的
     */
    @Override
    public Optional<HandleData> exec(HandleData handleData) {
        // 如果没有指定id，按照顺序进行匹配
        if (StringUtils.isNullOrEmpty(handleData.getHandleId())) {
            // 经过各个处理器处理的剩余数据
            for (String handleId : this.handlerMap.keySet()) {
                boolean isMatched = this.handlerMap.get(handleId).apply(handleData.getTransData());
                if (isMatched) {
                    handleData.setHandleId(handleId);
                    return Optional.of(handleData);
                }
            }
        } else {
            // 如果指定了handleId
            Function<TransData, Boolean> handler = this.handlerMap.get(handleData.getHandleId());
            boolean isMatched = handler.apply(handleData.getTransData());
            if (isMatched) {
                return Optional.of(handleData);
            }
        }
        return Optional.empty();
    }


}
