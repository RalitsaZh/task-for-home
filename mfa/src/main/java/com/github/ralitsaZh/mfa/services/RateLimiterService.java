package com.github.ralitsaZh.mfa.services;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.github.ralitsaZh.mfa.services.constants.Constants.MAX_ATTEMPTS;
import static com.github.ralitsaZh.mfa.services.constants.Constants.TIME_WINDOW;

@Service
public class RateLimiterService {

    private final RedisTemplate<String, Integer> redisTemplate;
    private final int maxAttempts;
    private final long timeWindow;

    public RateLimiterService(RedisTemplate<String, Integer> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.maxAttempts = MAX_ATTEMPTS;
        this.timeWindow = TIME_WINDOW;
    }

    public boolean isRateLimited(String email, String action) {
        String key = action + "_attempts:" + email;
        ValueOperations<String, Integer> ops = redisTemplate.opsForValue();

        Integer attempts = ops.get(key);
        if (attempts == null) {
            ops.set(key, 1, timeWindow, TimeUnit.MINUTES);
            return false;
        }

        if (attempts >= maxAttempts) {
            return true;
        }

        ops.increment(key);
        return false;
    }

    public boolean isVerifyRateLimited(String email) {
        return isRateLimited(email, "verify");
    }

    public boolean isSendRateLimited(String email) {
        return isRateLimited(email, "send");
    }
}
