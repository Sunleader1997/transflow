package org.sunyaxing.transflow.transflowapp.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChainManager {
    // 单例静态缓存
    public final static Map<String, TransFlowChain<?>> CHAIN_CACHE = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public static void start(){
        scheduler.scheduleAtFixedRate(()->{
            CHAIN_CACHE.forEach((nodeId, chain)->{
                chain.run();
            });
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    public static void addChainCache(TransFlowChain<?> chain) {
        if (CHAIN_CACHE.containsKey(chain.getNodeId())) {
            // 销毁线程
            CHAIN_CACHE.get(chain.getNodeId()).dispose();
            // 移除引用
            CHAIN_CACHE.remove(chain.getNodeId());
        }
        // 重新放到缓存
        CHAIN_CACHE.put(chain.getNodeId(), chain);
    }
    public static TransFlowChain<?> getChainCache(String nodeId) {
        return CHAIN_CACHE.get(nodeId);
    }
    public static boolean containsChain(String nodeId){
        return CHAIN_CACHE.containsKey(nodeId);
    }
}
