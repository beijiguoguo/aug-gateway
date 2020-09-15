package aug.common.gateway.config;

import aug.common.gateway.db.dao.DBConnector;
import aug.common.gateway.db.dao.RateLimitDAO;
import aug.common.gateway.db.dao.ServiceDAO;
import aug.common.gateway.handler.AfterRequestHandler;
import aug.common.gateway.handler.HttpApiHandler;
import aug.common.gateway.handler.SseHandler;
import aug.common.gateway.handler.WebSocketHandler;
import aug.common.gateway.helper.*;
import aug.common.gateway.mock.StreamMessageMock;
import aug.common.gateway.server.GatewayServer;
import aug.common.gateway.utils.StreamMsgPushUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 自定义bean配置
 *
 * @author guoxiaoyong
 * @date 2020/9/11
 */
@Configuration
@Import(value = DataSourceConfig.class)
public class BeansConfig {

    @Bean
    public ServiceDAO serviceDAO(DBConnector dbConnector) {
        return new ServiceDAO(dbConnector);
    }

    @Bean
    public RateLimitDAO rateLimitDAO(DBConnector dbConnector) {
        return new RateLimitDAO(dbConnector);
    }

    @Bean(initMethod = "start")
    public PropertiesHelper propertiesHelper(ServiceDAO serviceDAO, RateLimitDAO rateLimitDAO) {
        return new PropertiesHelper(serviceDAO, rateLimitDAO);
    }

    @Bean
    public RateLimitStorage rateLimitStorage(@Value("${ratelimit.strategy}") String strategy, RedisTemplate<String, String> redisTemplate) {
        if ("local".equalsIgnoreCase(strategy)) {
            return new LocalRateLimitStorage();
        } else if ("redis".equalsIgnoreCase(strategy)) {
            return new RedisRateLimitStorage(redisTemplate);
        } else {
            throw new RuntimeException(strategy + " not supported yet");
        }
    }

    @Bean
    public BlackListStorage blackListStorage(@Value("${blacklist.strategy}") String strategy, RedisTemplate<String, String> redisTemplate) {
        if ("local".equalsIgnoreCase(strategy)) {
            throw new RuntimeException(strategy + " not supported yet");
        } else if ("redis".equalsIgnoreCase(strategy)) {
            return new RedisBlackListStorage(redisTemplate);
        } else {
            throw new RuntimeException(strategy + " not supported yet");
        }
    }

    @Bean
    public HttpApiHandler httpApiHandler(PropertiesHelper propertiesHelper, BlackListStorage blackListStorage, RateLimitStorage rateLimitStorage) {
        return new HttpApiHandler(propertiesHelper, blackListStorage, rateLimitStorage);
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        WebSocketHandler webSocketHandler = new WebSocketHandler();
        StreamMsgPushUtils.addListener(webSocketHandler);
        return webSocketHandler;
    }

    @Bean
    public SseHandler sseHandler() {
        SseHandler sseHandler = new SseHandler();
        StreamMsgPushUtils.addListener(sseHandler);
        return sseHandler;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public GatewayServer gatewayServer(HttpApiHandler httpApiHandler, WebSocketHandler webSocketHandler, SseHandler sseHandler) {
        return new GatewayServer(8080, httpApiHandler, webSocketHandler, sseHandler);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public AfterRequestHandler afterRequestHandler(BlackListStorage blackListStorage) {
        return new AfterRequestHandler(blackListStorage);
    }

    @Bean(initMethod = "start")
    public StreamMessageMock webSocketMessageMock() {
        return new StreamMessageMock();
    }

}
