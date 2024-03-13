package com.bitharmony.comma.member.notification.repository;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class CompletableFutureRepository {
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
