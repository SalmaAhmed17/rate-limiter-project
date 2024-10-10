package com.example.taskJAVA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class redisService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    // Set the user action count and expiration time window in Redis
    public Long incrementActionsWithTTL(String key, int timeWindowSeconds) {
        Long currentActions = redisTemplate.opsForValue().increment(key);

        if (currentActions != null && currentActions == 1) {
            // If it's the first action, set the expiration for the key
            redisTemplate.expire(key, timeWindowSeconds, TimeUnit.SECONDS);
        }

        return currentActions;
    }

    // Check if the user has exceeded the allowed max actions
    public boolean isRateLimited(String key, int maxActions) {
        String actionCountStr = redisTemplate.opsForValue().get(key);
        if (actionCountStr != null) {
            int currentActions = Integer.parseInt(actionCountStr);
            return currentActions >= maxActions;
        }
        return false;
    }

    // Get the TTL for the Redis key to check remaining time window
    public Long getRemainingTTL(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
