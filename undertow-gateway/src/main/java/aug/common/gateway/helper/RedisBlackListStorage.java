package aug.common.gateway.helper;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author guoxiaoyong
 * @date 2020/9/11
 */
public class RedisBlackListStorage implements BlackListStorage {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisBlackListStorage(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void increment(String ip) {
        Long i = redisTemplate.boundValueOps(ip).increment(1);
        if (i == null || i == 1) {
            setExpiredAt(ip, 5, TimeUnit.SECONDS);
        }
        if (i != null && i >= 10) {
            // 加入黑名单
            String key = makeKey(ip);
            redisTemplate.boundValueOps(key).set("E");
            setExpiredAt(key, 50, TimeUnit.SECONDS);
        }
    }

    @Override
    public Boolean isForbidden(String ip) {
        return redisTemplate.hasKey(makeKey(ip));
    }

    @Override
    public void setExpiredAt(String key, long timeout, TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }

    private String makeKey(String ip) {
        return "GATEWAY:BLACKLIST:" + ip;
    }
}
