package aug.common.gateway.mock;

import aug.common.gateway.utils.StreamMsgPushUtils;
import aug.common.gateway.utils.StringUtils;
import aug.common.gateway.utils.ThreadPoolUtils;

import java.util.concurrent.TimeUnit;

/**
 * 推送模拟类
 *
 * @author guoxiaoyong
 * @date 2020/9/6
 */
public class StreamMessageMock {

    public void start() {
        ThreadPoolUtils.newSingleScheduledExecutor("StreamMessageMock")
                .scheduleAtFixedRate(() -> {
                    StreamMsgPushUtils.pushMsg(StringUtils.uuid() + "-" + System.currentTimeMillis());
                }, 5_000, 100, TimeUnit.MILLISECONDS);
    }
}
