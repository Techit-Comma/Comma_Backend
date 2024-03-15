package com.bitharmony.comma.member.notification.repository;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class CompletableFutureRepository { // 향후 수정이 있을 수도 있어 남겨둠.
    private final Map<String, CompletableFuture<String>> completableFutureMap = new ConcurrentHashMap<>();

    public void save(String key, CompletableFuture<String> completableFuture) {
        completableFutureMap.put(key, completableFuture);
    }

    public Optional<CompletableFuture<String>> findByKey(String key) {
        return Optional.ofNullable(completableFutureMap.get(key));
    }

    public void deleteAllByKey(String key) {
        completableFutureMap.keySet().stream()
                .filter(k -> k.startsWith(key))
                .map(completableFutureMap::remove);
    }
}
