package org.sunyaxing.transflow.extensions;

import javafx.util.Pair;
import org.pf4j.util.StringUtils;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TransFlowGateway extends ExtensionLifecycle {

    protected ExtensionContext extensionContext;

    public TransFlowGateway(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }

    /**
     * 网关模式
     * 按顺序，第一个handle先过滤，不符合条件的，交给下一个handle，
     * chain 需要调用的消费方法
     */
    @Override
    public List<HandleData> exec(HandleData handleData) {
        List<HandleData> handleDatas = new ArrayList<>();
        // 如果没有指定id，按照顺序进行匹配
        if (StringUtils.isNullOrEmpty(handleData.getHandleId())) {
            // 剩余数据
            AtomicReference<List<TransData>> remainData = new AtomicReference<>(handleData.getTransData());
            this.handleMap.forEach((handleId, handleValue) -> {
                if (remainData.get() != null && !remainData.get().isEmpty()) {
                    HandleData handleData1 = new HandleData(handleId, null);
                    Pair<List<TransData>, List<TransData>> pair = execDatasWithPair(handleValue, remainData.get());
                    remainData.set(pair.getValue());
                    if (!pair.getKey().isEmpty()) {
                        handleData1.setTransData(pair.getKey());
                        handleDatas.add(handleData1);
                    };
                }
            });
        } else {
            // 如果指定了handleId
            String handValue = this.handleMap.get(handleData.getHandleId());
            List<TransData> data = execDatas(handValue, handleData.getTransData());
            if (!data.isEmpty()) handleDatas.add(new HandleData(handleData.getHandleId(), data));
        }
        return handleDatas;
    }

    protected abstract Pair<List<TransData>, List<TransData>> execDatasWithPair(String handleValue, List<TransData> data);
}