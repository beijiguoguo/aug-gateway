package aug.common.gateway.helper;

import org.xnio.OptionMap;
import org.xnio.Xnio;
import org.xnio.XnioExecutor;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 基于内存的限流存储
 *
 * @author guoxiaoyong
 * @date 2020/9/9
 */
public class LocalRateLimitStorage implements RateLimitStorage {
    private final ConcurrentHashMap<String, AtomicLong> limits = new ConcurrentHashMap<>();

    @Override
    public long incrementAndGet(String key) {
        AtomicLong limitCount = limits.computeIfAbsent(key, s -> new AtomicLong(0));
        return limitCount.incrementAndGet();
    }

    @Override
    public void setExpiredAt(String key, long timeout, TimeUnit timeUnit) {
        try {
            XnioExecutor executor = Xnio.getInstance().createWorker(OptionMap.EMPTY).getIoThread();
            executor.executeAfter(() -> {
                limits.remove(key);
            }, timeout, timeUnit);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
