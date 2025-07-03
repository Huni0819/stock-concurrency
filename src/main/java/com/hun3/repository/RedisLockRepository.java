package com.hun3.repository;

import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisLockRepository {

    private RedisTemplate<String, String> redisTemplate;

    public RedisLockRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean lock(Long key) {

        return redisTemplate
            .opsForValue()
            .setIfAbsent(
                generateKey(key),
                "lock",
                Duration.ofMillis(3_000)
            );
    }

    public Boolean unLock(Long key) {
        return redisTemplate.delete(generateKey(key));
    }

    public String generateKey(Long key) {
        return key.toString();
    }
}
