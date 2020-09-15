package aug.common.gateway.helper;

import java.util.concurrent.TimeUnit;

/**
 * @author guoxiaoyong
 * @date 2020/9/9
 */
public interface RateLimitStorage {
    long incrementAndGet(String key);

    void setExpiredAt(String key, long timeout, TimeUnit timeUnit);
}
