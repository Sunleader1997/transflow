package org.sunyaxing.transflow.transflowapp.controllers;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.util.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@ServerEndpoint(value = "/transflow/event/{jobId}")
public class EventWsController implements ApplicationRunner {
    public static final Map<String, Session> SESSION_MAP = new HashMap<>();
    public static final LinkedBlockingDeque<String> msgQueue = new LinkedBlockingDeque<>(100);
    public static final AtomicBoolean hasWebsocket = new AtomicBoolean(false);

    @OnOpen
    public void onOpen(Session session) {
        log.info("EventWsController onOpen");
        hasWebsocket.set(true);
        SESSION_MAP.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("jobId") String jobId) {
        log.info("EventWsController onClose");
        SESSION_MAP.remove(session.getId());
        try {
            session.close();
        } catch (Exception e) {
            log.error("send message error", e);
        } finally {
            if (SESSION_MAP.isEmpty()) {
                hasWebsocket.set(false);
            }
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("EventWsController onMessage");
        log.info("message: {}", message);
    }

    public static synchronized void sendMessage(String message) {
        for (Session session : SESSION_MAP.values()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.error("send message error", e);
                try {
                    session.close();
                } catch (Exception e2) {
                    log.error("send message error", e2);
                }
            }
        }
    }

    public static void sendMessage(String key, String value) {
        if(hasWebsocket.get()){ // 在进入之前可重入锁之前排除锁的影响，观测状态下会降低吞吐
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", key);
            jsonObject.put("value", value);
            // 线程执行 offerLast 终究还是会进入可重入锁 影响性能
            // 但是在队列满之后仅判断了大小， 几乎不会影响性能
            msgQueue.offerLast(jsonObject.toJSONString());
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ThreadUtil.execute(() -> {
            // 多线程吐大量数据websocket会报错，必须单线程限流
            while (!Thread.currentThread().isInterrupted()) {
                ThreadUtil.sleep(msgQueue.size());// 吞吐量大时就降速，避免前端处理不过来
                try{
                    String queue = msgQueue.poll(1, TimeUnit.SECONDS);
                    if (StringUtils.isNotNullOrEmpty(queue)) {
                        sendMessage(queue);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
