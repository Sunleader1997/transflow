package org.sunyaxing.transflow.transflowapp.reactor;

import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.sunyaxing.transflow.extensions.TransFlowFilter;
import org.sunyaxing.transflow.extensions.TransFlowOutput;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
@DependsOn
public class FilterRunnable implements Runnable, Disposable, ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(FilterRunnable.class);

    private ExtensionContext extensionContext;
    private PluginManager pluginManager;
    private Flux<String> dataDequeue;
    private Scheduler processScheduler;
    private Scheduler dequeueScheduler;
    private Disposable disposable;
    private TransFlowFilter filter;
    private List<TransFlowOutput> outers;

    public FilterRunnable(ExtensionContext extensionContext, PluginManager pluginManager) {
        this.pluginManager = pluginManager;
        this.extensionContext = extensionContext;
        this.dataDequeue = Mono.defer(this::dequeue).repeat();
        this.processScheduler = Schedulers.newBoundedElastic(Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE, Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE, "data");
        this.dequeueScheduler = Schedulers.newSingle("dequeue");
    }

    private Mono<Void> dataFlowWithEachFilter(String data) {
        return Mono.using(() -> {
            log.info("处理： {}", data);
            var res = filter.exec(data);
            return Mono.just(res);
        }, dataBk -> Mono.fromRunnable(() -> {
            // 下发给 output
            CountDownLatch countDownLatch = new CountDownLatch(outers.size());
            outers.forEach(output -> {
                Thread.ofVirtual().start(() -> {
                    try {
                        output.output(dataBk);
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
            // 清理资源
            // commit or rollback
            log.info("提交数据");
        });
    }

    public Mono<String> dequeue() {
        String data = extensionContext.dequeue();
        if (data != null) {
            return Mono.just(data);
        } else {
            return Mono.empty();
        }
    }

    @Override
    public void run() {
        List<TransFlowFilter> filters = pluginManager.getExtensions(TransFlowFilter.class);
        this.filter = filters.stream().reduce(((filter1, filter2) -> {
            filter1.addNext(filter2);
            return filter1;
        })).orElse(null);
        this.outers = pluginManager.getExtensions(TransFlowOutput.class);

        this.disposable = Flux.<String>from(this.dataDequeue)
                .flatMap(data -> dataFlowWithEachFilter(data).subscribeOn(processScheduler), 10)
                .onErrorContinue((throwable, o) -> log.error("线程异常", throwable))
                .subscribeOn(dequeueScheduler)
                .subscribe();
    }

    @Override
    public void dispose() {
        this.disposable.dispose();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread.ofVirtual().start(this);
    }
}
