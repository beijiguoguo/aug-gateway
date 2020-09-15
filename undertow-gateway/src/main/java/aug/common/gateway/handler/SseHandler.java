package aug.common.gateway.handler;

import aug.common.gateway.utils.StreamMsgPushUtils;
import aug.common.gateway.utils.StringUtils;
import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.server.handlers.sse.ServerSentEventConnectionCallback;
import org.xnio.IoUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guoxiaoyong
 * @date 2020/9/8
 */
public class SseHandler implements ServerSentEventConnectionCallback, StreamMsgPushUtils.StreamMsgListener {

    private final ConcurrentHashMap<String, ServerSentEventConnection> clients = new ConcurrentHashMap<>();

    @Override
    public void connected(ServerSentEventConnection serverSentEventConnection, String s) {
        try {
            String clientId = StringUtils.uuid();
            serverSentEventConnection.setKeepAliveTime(5_000);
            serverSentEventConnection.addCloseTask(w -> clients.remove(clientId));
            clients.put(clientId, serverSentEventConnection);
        } catch (Exception e) {
            IoUtils.safeClose(serverSentEventConnection);
        }
    }

    @Override
    public void onMessage(String message) {
        clients.values().forEach(
                connection -> {
                    connection.send(message, EVENT_CALLBACK);
                }
        );
    }

    private static final ServerSentEventConnection.EventCallback EVENT_CALLBACK = new ServerSentEventConnection.EventCallback() {
        @Override
        public void done(ServerSentEventConnection serverSentEventConnection, String s, String s1, String s2) {
        }

        @Override
        public void failed(ServerSentEventConnection serverSentEventConnection, String s, String s1, String s2, IOException e) {
            IoUtils.safeClose(serverSentEventConnection);
        }
    };

}
