package aug.common.gateway.server;

import aug.common.gateway.handler.HttpApiHandler;
import aug.common.gateway.handler.SseHandler;
import aug.common.gateway.handler.WebSocketHandler;
import aug.common.gateway.utils.HttpUtils;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;

/**
 * 网关核心服务器
 *
 * @author guoxiaoyong
 * @date 2020/8/13
 */
public class GatewayServer {

    private Undertow server;

    private final int port;
    private final HttpApiHandler httpApiHandler;
    private final WebSocketHandler webSocketHandler;
    private final SseHandler sseHandler;


    public GatewayServer(int port, HttpApiHandler httpApiHandler, WebSocketHandler webSocketHandler, SseHandler sseHandler) {
        this.port = port;
        this.httpApiHandler = httpApiHandler;
        this.webSocketHandler = webSocketHandler;
        this.sseHandler = sseHandler;
    }

    public void start() {
        PathHandler pathHandler =
                // 默认处理器，匹配不到路径的时候走这里
                Handlers.path(HttpUtils::send404Error)
                        // http请求处理器
                        .addPrefixPath("/api", httpApiHandler)
                        // websocket请求处理器
                        .addPrefixPath("/ws", Handlers.websocket(webSocketHandler))
                        // sse请求处理器
                        .addPrefixPath("/sse", Handlers.serverSentEvents(sseHandler));

        server = Undertow.builder()
                .addHttpListener(port, "0.0.0.0")
                .setHandler(pathHandler)
                .build();
        server.start();

        System.out.println("Gateway server started at " + port);
    }

    public void stop() {
        server.stop();
    }

}
