package aug.common.gateway.utils;

import com.google.common.collect.Lists;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * 消息推送工具
 *
 * @author guoxiaoyong
 * @date 2020/9/10
 */
public class StreamMsgPushUtils {

    private static final List<StreamMsgListener> LISTENERS = Lists.newArrayList();

    public static void addListener(@NonNull StreamMsgListener listener) {
        LISTENERS.add(listener);
    }

    public static void pushMsg(@NonNull String message) {
        LISTENERS.forEach(listener -> listener.onMessage(message));
    }

    /**
     * 消息回调接口
     */
    public interface StreamMsgListener {
        void onMessage(String message);
    }

}
