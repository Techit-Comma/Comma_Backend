package com.bitharmony.comma.member.notification.repository;


import com.bitharmony.comma.member.notification.dto.NotificationResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

@Component
public class DeferredResultRepository {
    private final Map<String, DeferredResult<List<NotificationResponse>>> deferredResultMap = new ConcurrentHashMap<>();

    public void save(String key, DeferredResult<List<NotificationResponse>> deferredResult) {
        deferredResultMap.put(key, deferredResult);
    }

    public Optional<DeferredResult<List<NotificationResponse>>> findByKey(String key) {
        return Optional.ofNullable(deferredResultMap.get(key));
    }

    public void deleteByKey(String key) {
        deferredResultMap.remove(key);
    }
}
