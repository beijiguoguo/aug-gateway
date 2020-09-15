package aug.common.gateway.helper;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 基于redis的限流存储
 *
 * @author guoxiaoyong
 * @date 2020/9/10
 */
public class RedisRateLimitStorage implements RateLimitStorage {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisRateLimitStorage(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public long incrementAndGet(String key) {
        Long i = redisTemplate.boundValueOps(key).increment(1);
        return i == null ? 1 : i;
    }

    @Override
    public void setExpiredAt(String key, long timeout, TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }
}
