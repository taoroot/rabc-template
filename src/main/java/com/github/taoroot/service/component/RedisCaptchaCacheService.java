package com.github.taoroot.service.component;

import com.github.taoroot.common.core.utils.CaptchaCacheService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class RedisCaptchaCacheService implements CaptchaCacheService {
    private final StringRedisTemplate redisTemplate;

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        redisTemplate.opsForValue().set(key, value, expiresInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean exists(String key) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        Boolean aBoolean = redisTemplate.hasKey(key);
        if (aBoolean == null) {
            return false;
        }
        return aBoolean;
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
