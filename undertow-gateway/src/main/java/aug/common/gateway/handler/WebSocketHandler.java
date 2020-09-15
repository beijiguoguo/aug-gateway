package aug.common.gateway.handler;

import aug.common.gateway.utils.JsonUtils;
import aug.common.gateway.utils.StreamMsgPushUtils;
import aug.common.gateway.utils.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.xnio.IoUtils;
import org.xnio.XnioExecutor;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author guoxiaoyong
 * @date 2020/9/6
 */
public class WebSocketHandler implements WebSocketConnectionCallback, StreamMsgPushUtils.StreamMsgListener {

    private final ConcurrentHashMap<String, WebSocketClient> clients = new ConcurrentHashMap<>();

    @Override
    public void onConnect(WebSocketHttpExchange webSocketHttpExchange, WebSocketChannel webSocketChannel) {
        String clientId = StringUtils.uuid();
        WebSocketClient client =
                new WebSocketClient()
                        .setClientId(clientId)
                        .setWebSocketChannel(webSocketChannel)
                        .setLastHeartbeatAt(System.currentTimeMillis());
        clients.put(clientId, client);

        webSocketChannel.getReceiveSetter().set(new AbstractReceiveListener() {
            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                JsonNode jsonNode = JsonUtils.parseNode(message.getData());
                JsonNode pong = jsonNode.get("pong");
                if (pong != null) {
                    Long lastHeartbeatAt = client.getLastHeartbeatAt();
                    if (pong.longValue() != lastHeartbeatAt) {
                        IoUtils.safeClose(channel);
                    }
                }
            }
        });
        webSocketChannel.resumeReceives();

        //5s心跳
        XnioExecutor.Key pingExecutor = webSocketChannel.getIoThread().executeAtInterval(() -> heartbeat(client), 5, TimeUnit.SECONDS);

        webSocketChannel.addCloseTask(w -> {
            pingExecutor.remove();
            clients.remove(clientId);
        });
    }

    private void heartbeat(WebSocketClient client) {
        long now = System.currentTimeMillis();
        WebSocketChannel webSocketChannel = client.getWebSocketChannel();
        WebSockets.sendText(
                ByteBuffer.wrap(JsonUtils.toJsonStringPretty(new WsHeartbeat(now)).getBytes(StandardCharsets.UTF_8)),
                webSocketChannel,
                WEB_SOCKET_CALLBACK);
        client.setLastHeartbeatAt(now);
    }

    private final static WebSocketCallback<Void> WEB_SOCKET_CALLBACK = new WebSocketCallback<Void>() {
        @Override
        public void complete(WebSocketChannel channel, Void context) {
        }

        @Override
        public void onError(WebSocketChannel channel, Void context, Throwable throwable) {
            IoUtils.safeClose(channel);
        }
    };


    @Override
    public void onMessage(String message) {
        clients.values().forEach(
                webSocketClient -> {
                    WebSockets.sendText(
                            ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)),
                            webSocketClient.getWebSocketChannel(),
                            WEB_SOCKET_CALLBACK);
                });
    }

    private static class WsHeartbeat {
        public final Long ping;

        public WsHeartbeat(Long ping) {
            this.ping = ping;
        }
    }

    private static class WebSocketClient {
        private String clientId;
        private WebSocketChannel webSocketChannel;
        private Long lastHeartbeatAt;

        public String getClientId() {
            return clientId;
        }

        public WebSocketClient setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public WebSocketChannel getWebSocketChannel() {
            return webSocketChannel;
        }

        public WebSocketClient setWebSocketChannel(WebSocketChannel webSocketChannel) {
            this.webSocketChannel = webSocketChannel;
            return this;
        }

        public Long getLastHeartbeatAt() {
            return lastHeartbeatAt;
        }

        public WebSocketClient setLastHeartbeatAt(Long lastHeartbeatAt) {
            this.lastHeartbeatAt = lastHeartbeatAt;
            return this;
        }

    }
}
