package org.sunyaxing.transflow.extensions.base.typesimpl;

import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.types.TransFlowMiddleHandler;

import java.util.Optional;
import java.util.function.Function;

/**
 * 中间处理器-gateway模式
 * 中间处理器返回 bool 结果 ，match 则向下一个节点推送, 否则下一个处理器处理
 * 处理器不会改变数据格式
 * 同时拥有输入输出
 * 通过处理器决定是否往后发送数据
 */
public abstract class TransFlowMiddleGatewayHandler<T> extends TransFlowMiddleHandler<T, Boolean, T> {
    private static final Logger log = LoggerFactory.getLogger(TransFlowMiddleGatewayHandler.class);


    public TransFlowMiddleGatewayHandler(ExtensionContext extensionContext) {
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
    public Optional<HandleData<T>> exec(HandleData<T> handleData) {
        // 如果没有指定处理器id，则按照顺序进行匹配
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
            Function<TransData<T>, Boolean> handler = this.handlerMap.get(handleData.getHandleId());
            boolean isMatched = handler.apply(handleData.getTransData());
            if (isMatched) {
                return Optional.of(handleData);
            }
        }
        return Optional.empty();
    }


}
