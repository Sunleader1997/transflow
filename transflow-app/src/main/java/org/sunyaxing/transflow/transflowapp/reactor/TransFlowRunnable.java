package org.sunyaxing.transflow.transflowapp.reactor;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Getter;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.extensions.TransFlowMultiInput;
import org.sunyaxing.transflow.transflowapp.common.TransFlowChain;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;

public class TransFlowRunnable implements Runnable, Disposable {

    private static final Logger log = LoggerFactory.getLogger(TransFlowRunnable.class);

    private final Flux<HandleData> dataDequeue;
    private final Scheduler processScheduler;
    private final Scheduler dequeueScheduler;
    private Disposable disposable;
    private final TransFlowInput input;
    @Getter
    private final TransFlowChain<TransFlowInput> chain;

    public TransFlowRunnable(TransFlowChain<TransFlowInput> chain) {
        this.input = chain.getCurrentNode();
        this.chain = chain;
        this.dataDequeue = Flux.defer(this::dequeue).repeat();
        this.processScheduler = Schedulers.newBoundedElastic(Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE, Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE, "data");
        this.dequeueScheduler = Schedulers.newSingle("dequeue");
    }

    private Mono<Void> dataFlowWithEachFilter(HandleData handleData) {
        return Mono.using(() -> {
            return Mono.just(handleData);
        }, dataBk -> Mono.fromRunnable(() -> {
            // 交给 chain 处理
            this.chain.exec(handleData.getHandleId(), handleData.getTransData());
        }), dataBk -> {
            input.commit(handleData);
        });
    }

    public Publisher<HandleData> dequeue() {
        if (input instanceof TransFlowMultiInput) {
            List<HandleData> handleData = ((TransFlowMultiInput) input).handleDequeue();
            if (CollectionUtil.isEmpty(handleData)) {
                return Mono.empty();
            } else {
                return Flux.just(handleData.toArray(new HandleData[0]));
            }
        } else {
            HandleData datas = input.dequeue();
            if (datas != null) {
                return Mono.just(datas);
            } else {
                return Mono.empty();
            }
        }
    }

    @Override
    public void run() {
        this.disposable = Flux.from(this.dataDequeue)
                .flatMap(datas -> dataFlowWithEachFilter(datas).subscribeOn(processScheduler), 10)
                .onErrorContinue((throwable, o) -> log.error("线程异常", throwable))
                .subscribeOn(dequeueScheduler)
                .subscribe();
    }

    @Override
    public void dispose() {
        try {
            this.chain.dispose();
        } catch (Exception e) {
            log.error("销毁chain 异常", e);
        }
        try {
            this.disposable.dispose();
        } catch (Exception e) {
            log.error("销毁线程异常", e);
        }
    }
}
