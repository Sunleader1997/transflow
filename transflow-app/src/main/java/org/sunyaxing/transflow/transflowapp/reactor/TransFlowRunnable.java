package org.sunyaxing.transflow.transflowapp.reactor;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.extensions.base.types.TransFlowInput;
import org.sunyaxing.transflow.transflowapp.common.TransFlowChain;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class TransFlowRunnable {

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
        // 手动结束 dequeue 线程后，执行 this.chain::dispose 销毁 input chain
        this.dataDequeue = Flux.<HandleData>defer(this.input::dequeue).repeat().doOnCancel(this.chain::dispose);
        this.processScheduler = Schedulers.newBoundedElastic(Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE, Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE, "data");
        this.dequeueScheduler = Schedulers.newSingle("dequeue");
    }

    private Mono<Void> dataFlowWithEachFilter(HandleData handleData) {
        return Mono.using(() -> {
            return Mono.just(handleData);
        }, dataBk -> Mono.fromRunnable(() -> {
            // 交给 chain 处理
            this.chain.handle(handleData);
        }), dataBk -> {
            this.input.commit(handleData);
        });
    }

    public void run() {
        this.disposable = Flux.from(this.dataDequeue)
                .flatMap(datas -> dataFlowWithEachFilter(datas).subscribeOn(processScheduler), 10)
                .onBackpressureBuffer(3, (data) -> {
                    // TODO 待实现的反压策略
                    // 主动拉取时，无法触发反压，只有被动推才会触发
                    log.error("压力过大 {}", data);
                })
                .onErrorContinue((throwable, o) -> log.error("线程异常", throwable))
                .doOnCancel(() -> {  // 手动结束时触发
                    log.info("doOnCancel");
                })
                .subscribeOn(dequeueScheduler)
                .subscribe();
    }

    /**
     * 销毁线程
     *
     * @param safe 是否安全关闭
     *             true 安全关闭，不会销毁整个链路数据传输
     *             false 强制关闭，直接销毁整个链路数据传输
     */
    public void dispose(boolean safe) {
        try {
            // 销毁 input 线程，不再产生数据
            this.input.destroy();
        } catch (Exception e) {
            log.error("销毁 input 异常", e);
        }
        try {
            if (!safe) {
                // 结束 数据链路传输
                this.disposable.dispose();
            }
        } catch (Exception e) {
            log.error("销毁线程异常", e);
        }
    }
}
