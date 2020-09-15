package aug.common;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.server.handlers.sse.ServerSentEventHandler;
import io.undertow.util.StringReadChannelListener;

import java.io.IOException;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.resource;
import static io.undertow.Handlers.serverSentEvents;

/**
 * Created by guoxiaoyong on 2020/8/13.
 */
public class ServerSentEventServer {

    public static void main(final String[] args) {
        final ServerSentEventHandler sseHandler = serverSentEvents();
        HttpHandler chatHandler = new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                new StringReadChannelListener(exchange.getConnection().getByteBufferPool()) {

                    @Override
                    protected void stringDone(String string) {
                        for(ServerSentEventConnection h : sseHandler.getConnections()) {
                            h.send("Hello");
                        }
                    }

                    @Override
                    protected void error(IOException e) {

                    }
                }.setup(exchange.getRequestChannel());
            }
        };
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(path()
                        .addPrefixPath("/sse", sseHandler)
                        .addPrefixPath("/send", chatHandler)
                        .addPrefixPath("/", resource(new ClassPathResourceManager(ServerSentEventServer.class.getClassLoader(), ServerSentEventServer.class.getPackage())).addWelcomeFiles("index.html")))
                .build();
        server.start();
    }
}
