package org.sunyaxing.transflow.extensions;

import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import org.sunyaxing.transflow.extensions.handlers.Handler;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
    public List<HandleData> exec(HandleData handleData) {
        List<HandleData> handleDatas = new ArrayList<>();
        // 如果没有指定id，按照顺序进行匹配
        if (StringUtils.isNullOrEmpty(handleData.getHandleId())) {
            // 经过各个处理器处理的剩余数据
            AtomicReference<List<TransData>> remainData = new AtomicReference<>(handleData.getTransData());
            this.handlerMap.forEach((handleId, handler) -> {
                if (remainData.get() != null && !remainData.get().isEmpty()) {
                    // 已经匹配的数据封装好准备交给下一个节点
                    Map.Entry<List<TransData>, List<TransData>> pair = execDatasWithPair(handler, handleData.getTransData());
                    if (!pair.getKey().isEmpty()){
                        handleDatas.add(new HandleData(handleId, pair.getKey()));
                    }
                    // 剩余数据交给下一个处理器
                    remainData.set(pair.getValue());
                }else{
                    log.info("数据已处理完");
                }
            });
        } else {
            // 如果指定了handleId
            Handler<TransData,Boolean> handler = this.handlerMap.get(handleData.getHandleId());
            Map.Entry<List<TransData>, List<TransData>> pair = execDatasWithPair(handler, handleData.getTransData());
            if (!pair.getKey().isEmpty()) handleDatas.add(new HandleData(handleData.getHandleId(), pair.getKey()));
        }
        return handleDatas;
    }


    /**
     * 调用 handler 的处理器方法，将结果为ture的数据给Key, false的数据给 value
     *
     * @param handler 处理器
     * @param datas   分配到该节点的数据
     * @return key-匹配的数据（交给下一个节点的数据） value-未匹配的数据（交给下一个处理器的数据）
     */
    protected Map.Entry<List<TransData>, List<TransData>> execDatasWithPair(Handler<TransData,Boolean> handler, List<TransData> datas) {
        List<TransData> result = new ArrayList<>();
        List<TransData> remain = new ArrayList<>();
        if (Objects.isNull(handler)) {
            return new AbstractMap.SimpleEntry<>(result, datas);
        }
        // 调用对应的处理器去处理数据
        // Handler handler = this.handlerMap.get(handleId);
        // 将批量数据逐个分给处理器去匹配处理
        // 匹配到的数据放入result中，未匹配到的数据放入remain中
        datas.forEach(transData -> {
            boolean handleRes = false;
            try {
                // 返回
                handleRes = handler.resolve(transData);
            } catch (Exception e) {
                log.error("脚本执行异常", e);
            } finally {
                if (handleRes) {
                    result.add(transData);
                } else {
                    remain.add(transData);
                }
            }
        });
        return new AbstractMap.SimpleEntry<>(result, remain);
    }
}
