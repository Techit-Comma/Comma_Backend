package com.bitharmony.comma.member.notification.repository;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmitterRepository {
    private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public void save(String key, SseEmitter sseEmitter) {
        sseEmitterMap.put(key, sseEmitter);
    }

    public Optional<SseEmitter> findByKey(String key) {
        return Optional.ofNullable(sseEmitterMap.get(key));
    }

    public List<SseEmitter> findAllByKey(String key){
        return sseEmitterMap.keySet().stream()
                .filter(k -> k.startsWith(key))
                .map(sseEmitterMap::get)
                .toList();
    }

    public void deleteByKey(String key) {
        sseEmitterMap.remove(key);
    }
}
