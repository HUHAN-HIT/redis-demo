package com.example.demo.config;

import com.example.demo.entity.InfoEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
public class RedisConfig {
    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public ReactiveRedisTemplate<String, String> redisStringTemplate(@Qualifier("reactiveRedisConnectionFactory")
                                                                         ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.string());
    }

    @Bean
    public ReactiveRedisTemplate<String, InfoEntity> redisInfoTemplate(@Qualifier("reactiveRedisConnectionFactory")
                                                                           ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext<String, InfoEntity> serializationContext =
            RedisSerializationContext.<String, InfoEntity>newSerializationContext()
                .key(StringRedisSerializer.UTF_8)
                .value(new Jackson2JsonRedisSerializer<>(InfoEntity.class))
                .hashKey(StringRedisSerializer.UTF_8)
                .hashValue(new Jackson2JsonRedisSerializer<>(InfoEntity.class))
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }
}
