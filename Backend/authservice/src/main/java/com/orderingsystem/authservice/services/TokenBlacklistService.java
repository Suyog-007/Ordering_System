package com.orderingsystem.authservice.services;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private final static String PREFIX = "blacklisted_token:";

    public TokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String token, Duration expiration) {
        redisTemplate.opsForValue().set(PREFIX + token, "true", expiration);
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
    }
}

