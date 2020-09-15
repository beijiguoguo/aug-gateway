package aug.common.gateway.handler;

import aug.common.gateway.helper.BlackListStorage;
import aug.common.gateway.utils.HttpUtils;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

/**
 * @author guoxiaoyong
 * @date 2020/9/11
 */
public class AfterRequestHandler {
    private Disposable subscribe;
    private final BlackListStorage blackListStorage;

    public AfterRequestHandler(BlackListStorage blackListStorage) {
        this.blackListStorage = blackListStorage;
    }

    public void start() {
        Flowable<String> processor = HttpUtils.getProcessor();
        subscribe = processor.subscribe(blackListStorage::increment);
    }

    public void stop() {
        if (subscribe != null) {
            subscribe.dispose();
        }
    }
}
