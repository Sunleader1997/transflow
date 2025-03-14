package org.sunyaxing.transflow.transflowapp.reactor;

import cn.hutool.core.date.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.transflowapp.common.TransFlowChain;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TransFlowRunnable implements Runnable, Disposable {

    private static final Logger log = LoggerFactory.getLogger(TransFlowRunnable.class);

    private final Flux<List<TransData>> dataDequeue;
    private final Scheduler processScheduler;
    private final Scheduler dequeueScheduler;
    private Disposable disposable;
    private final TransFlowInput input;
    private final TransFlowChain<TransFlowInput> chain;
    private final Map<String, TransFlowChain<?>> allNodes;

    public TransFlowRunnable(TransFlowChain<TransFlowInput> chain) {
        this.input = chain.getCurrentNode();
        this.chain = chain;
        this.dataDequeue = Mono.defer(this::dequeue).repeat();
        this.processScheduler = Schedulers.newBoundedElastic(Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE, Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE, "data");
        this.dequeueScheduler = Schedulers.newSingle("dequeue");
        this.allNodes = new HashMap<>();
        this.chain.chains(this.allNodes);
    }

    public TransFlowChain<?> getChainByNodeId(String nodeId) {
        return allNodes.get(nodeId);
    }

    private Mono<Void> dataFlowWithEachFilter(List<TransData> datas) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("等待处理");
        return Mono.using(() -> {
            // input 取一个资源
            log.info("处理： {}", datas.size());
            return Mono.just(datas);
        }, dataBk -> Mono.fromRunnable(() -> {
            stopWatch.stop();
            stopWatch.start("chain 处理");
            // 交给 chain 处理
            chain.exec(datas);
            stopWatch.stop();
            log.info("处理完成：{}", stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        }), dataBk -> {
            datas.forEach(data -> {
                input.commit(data.offset());
            });
        });
    }

    public Mono<List<TransData>> dequeue() {
        List<TransData> datas = input.dequeue();
        if (datas != null) {
            return Mono.just(datas);
        } else {
            return Mono.empty();
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
        this.disposable.dispose();
    }
}
