package com.biubiu.support.cache;

import com.biubiu.support.handler.DynamicSchedulingHandler;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Map;

/**
 * Created by zhanghaibiao on 2017/10/17.
 */
@Component
public class CustomCacheHolder {

    private final Map<String, Integer> webSocketCache = Maps.newConcurrentMap();

    private final Map<String, LinkedList<String>> simpSessionIdCache = Maps.newConcurrentMap();

    @Autowired
    private DynamicSchedulingHandler handler;

    @Autowired
    private DbService dbService;

    public int get(String key) {
        if (empty()) {
            return 0;
        }
        return webSocketCache.getOrDefault(key, 0);
    }

    public void put(String sessionId, String key, Object obj, String cron) {
        cacheSessionValue(sessionId, key);
        put(key, obj, cron);
    }

    private void cacheSessionValue(String sessionId, String value) {
        LinkedList<String> init = simpSessionIdCache.get(sessionId);
        init.add(value);
    }

    private void put(String key, Object obj, String cron) {
        Integer value = webSocketCache.get(key);
        if (value == null) {
            synchronized (CustomCacheHolder.class) {
                if (value == null) {
                    webSocketCache.put(key, 1);
                    handler.addTriggerTask(key,
                            new TriggerTask(() -> dbService.query((InquiryRequest) obj), new CronTrigger(cron)));
                } else {
                    int tmp = webSocketCache.get(key);
                    webSocketCache.put(key, ++tmp);
                }
            }
        } else {
            webSocketCache.put(key, ++value);
        }
    }

    private boolean remove(String key) {
        if (empty()) return true;
        if (get(key) <= 1) {
            webSocketCache.remove(key);
            return true;
        }
        int value = webSocketCache.get(key);
        webSocketCache.put(key, --value);
        return false;
    }

    private boolean empty() {
        return webSocketCache.size() == 0;
    }

    public void putSession(String sessionId) {
        simpSessionIdCache.putIfAbsent(sessionId, new LinkedList<>());
    }

    public void removeSession(String sessionId) {
        LinkedList<String> subscribeList = simpSessionIdCache.get(sessionId);
        subscribeList.forEach((webSocket) -> {
            if (remove(webSocket)) {
                if (handler.hasTask(webSocket)) {
                    handler.cancelTriggerTask(webSocket);
                }
            }
        });
        simpSessionIdCache.remove(sessionId);
    }

}
