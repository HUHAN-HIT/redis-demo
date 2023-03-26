package com.example.demo.controller;

import com.example.demo.entity.CreateUserRequestBody;
import com.example.demo.entity.InfoEntity;
import com.example.demo.helper.RedisHelper;
import com.sun.javafx.collections.UnmodifiableObservableMap;
import io.lettuce.core.Limit;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@RequestMapping("/login")
public class CacheController {
    private final RedisHelper redisHelper;

    @Autowired
    CacheController(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @PostMapping("/user")
    public Mono<Map<String, List<InfoEntity>>> createUser(@RequestBody CreateUserRequestBody requestBody) {
        InfoEntity info = new InfoEntity()
            .withPassword(requestBody.getPassword())
            .withName(requestBody.getUserName());
        Map<String, List<InfoEntity>> map = new HashMap<>();
        return redisHelper.save(requestBody.getUserName(), Collections.singletonList(info))
            .then(redisHelper.get(info.getName())
                .map(result -> {
                    map.put("user", result);
                    return map;
                }));
    }

    @GetMapping("/user/{user_id}")
    public Flux<Map<String, InfoEntity>> showUser(@Param("user_id")String userId) {
        Map<String, InfoEntity> map = new HashMap<>();
        return redisHelper.getCacheOrLoad("userId",
            () -> Mono.just(Collections.singletonList(new InfoEntity().withName(userId).withPassword("123456"))))
            .flatMapMany(Flux::fromIterable).take(1)
            .map(info -> {
                map.put("user", info);
                return map;
            });
    }

    @DeleteMapping("/user/{user_id}")
    public Mono<Void> deleteUser(@Param("user_id")String userId) {
        return redisHelper.delete(userId).then();
    }
}