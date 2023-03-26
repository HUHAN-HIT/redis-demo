package com.example.demo.helper;

import com.example.demo.entity.InfoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Component
public class RedisHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisHelper.class);

    private final ReactiveHashOperations<String, String, List<InfoEntity>> hashOps;

    @Autowired
    RedisHelper(ReactiveRedisTemplate<String, InfoEntity> template) {
        this.hashOps = template.opsForHash();
    }

    public Mono<List<InfoEntity>> getCacheOrLoad(String key, Supplier<Mono<List<InfoEntity>>> supplier) {
        return this.get(key)
            .switchIfEmpty(supplier.get()
                .flatMap(infoEntities -> this.save(key, infoEntities)
                    .thenReturn(infoEntities)))
            .onErrorResume(e -> {
                LOGGER.warn("get cache: {} or load failed", key, e);
                return Mono.empty();
            });
    }

    public Mono<List<InfoEntity>> get(String key) {
        return this.hashOps.get(key, "name")
            .onErrorResume(e -> {
                LOGGER.warn("get key: {} failed", key, e);
                return Mono.empty();
            });
    }

    public Mono<Boolean> save(String key, List<InfoEntity> infoEntities) {
        return this.hashOps.put(key, "name", infoEntities)
            .onErrorResume(e -> {
                LOGGER.warn("save key: {}", key, e);
                return Mono.empty();
            });
    }

    public Mono<Boolean> delete(String key) {
        return this.hashOps.delete(key)
            .onErrorResume(e -> {
                LOGGER.warn("delete key: {} failed", key, e);
                return Mono.empty();
            });
    }
}
