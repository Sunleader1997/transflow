package org.sunyaxing.transflow.transflowapp.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.TransFlowFilter;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.extensions.TransFlowOutput;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TransFlowRunnable implements Runnable, Disposable {

    private static final Logger log = LoggerFactory.getLogger(TransFlowRunnable.class);

    private Flux<List<TransData>> dataDequeue;
    private Scheduler processScheduler;
    private Scheduler dequeueScheduler;
    private Disposable disposable;
    private TransFlowInput input;
    private TransFlowFilter filter;
    private List<TransFlowOutput> outers;

    public TransFlowRunnable(TransFlowInput input, TransFlowFilter filter, List<TransFlowOutput> outers) {
        this.input = input;
        this.filter = filter;
        this.outers = outers;
        this.dataDequeue = Mono.defer(this::dequeue).repeat();
        this.processScheduler = Schedulers.newBoundedElastic(Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE, Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE, "data");
        this.dequeueScheduler = Schedulers.newSingle("dequeue");
    }

    private Mono<Void> dataFlowWithEachFilter(List<TransData> datas) {
        return Mono.using(() -> {
            // input 取一个资源
            log.info("处理： {}", datas.size());
            return Mono.just(datas);
        }, dataBk -> Mono.fromRunnable(() -> {
            // 交给 filter 处理
            var res = filter.exec(datas);
            // 下发给 output
            CountDownLatch countDownLatch = new CountDownLatch(outers.size());
            outers.forEach(output -> {
                Thread.ofVirtual().start(() -> {
                    try {
                        output.output(res);
                    } catch (Exception e) {
                        log.error("输出异常", e);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                log.error("线程异常", e);
            }
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
