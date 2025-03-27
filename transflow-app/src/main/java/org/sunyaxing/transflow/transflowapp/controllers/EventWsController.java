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

@Slf4j
@Component
@ServerEndpoint(value = "/transflow/event/{jobId}")
public class EventWsController implements ApplicationRunner {
    public static final Map<String, Session> SESSION_MAP = new HashMap<>();
    public static final LinkedBlockingDeque<String> msgQueue = new LinkedBlockingDeque<>(100);

    @OnOpen
    public void onOpen(Session session) {
        log.info("EventWsController onOpen");
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", key);
        jsonObject.put("value", value);
        msgQueue.offerLast(jsonObject.toJSONString());
    }

    public static Map<String, Long> TIME_CACHE = new ConcurrentHashMap<>();

    public static synchronized void sendMessageByCache(String eventType, String key, String value) {
        if (TIME_CACHE.containsKey(eventType)) {
            long time = TIME_CACHE.get(eventType);
            long now = System.currentTimeMillis();
            if (now - time < 2000) {
                return;
            } else {
                TIME_CACHE.put(eventType, now);
                sendMessage(key, value);
            }
        } else {
            long now = System.currentTimeMillis();
            TIME_CACHE.put(eventType, now);
            sendMessage(key, value);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (!Thread.currentThread().isInterrupted()) {
            ThreadUtil.sleep(msgQueue.size());
            String queue = msgQueue.poll(1, TimeUnit.SECONDS);
            if (StringUtils.isNotNullOrEmpty(queue)) {
                sendMessage(queue);
            }
        }
    }
}
