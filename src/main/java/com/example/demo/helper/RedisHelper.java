package com.example.demo.helper;

import com.example.demo.entity.InfoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Supplier;

@Component
public class RedisHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisHelper.class);

    private final ReactiveRedisTemplate<String, InfoEntity> redisInfoTemplate;

    @Autowired
    RedisHelper(ReactiveRedisTemplate<String, InfoEntity> template) {
        this.redisInfoTemplate = template;
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
        return this.redisInfoTemplate.opsForList().range(key, 0, -1).collectList()
            .onErrorResume(e -> {
                LOGGER.warn("get key: {} failed", key, e);
                return Mono.empty();
            });
    }

    public Mono<Long> save(String key, List<InfoEntity> infoEntities) {
        return this.delete(key)
            .then(this.redisInfoTemplate.opsForList().rightPushAll(key, infoEntities));
    }

    public Mono<Long> delete(String key) {
        return this.redisInfoTemplate.delete(key);
    }
}
