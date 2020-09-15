package aug.common.gateway.utils;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 线程池工具
 *
 * @author guoxiaoyong
 * @date 2020/9/8
 */
public class ThreadPoolUtils {
    public static ScheduledExecutorService newSingleScheduledExecutor(String threadName) {
        return new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder().namingPattern(threadName).build());
    }

}
