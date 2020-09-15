package aug.common.gateway.helper;

import java.util.concurrent.TimeUnit;

/**
 * @author guoxiaoyong
 * @date 2020/9/9
 */
public interface BlackListStorage {
    void increment(String key);

    Boolean isForbidden(String key);

    void setExpiredAt(String key, long timeout, TimeUnit timeUnit);
}
