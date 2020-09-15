package aug.common.gateway.handler;

import aug.common.gateway.db.po.RateLimit;
import aug.common.gateway.db.po.ServiceInfo;
import aug.common.gateway.consts.ErrorCode;
import aug.common.gateway.consts.ServiceCode;
import aug.common.gateway.helper.BlackListStorage;
import aug.common.gateway.helper.PropertiesHelper;
import aug.common.gateway.helper.RateLimitStorage;
import aug.common.gateway.utils.HttpUtils;
import aug.common.gateway.utils.IpUtils;
import aug.common.gateway.utils.JwtUtils;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * http接口抽象处理器
 *
 * @author guoxiaoyong
 * @date 2020/9/9
 */
public abstract class AbstractHttpHandler implements HttpHandler {

    private final PropertiesHelper propertiesHelper;
    private final BlackListStorage blackListStorage;
    private final RateLimitStorage rateLimitStorage;

    protected AbstractHttpHandler(PropertiesHelper propertiesHelper, BlackListStorage blackListStorage, RateLimitStorage rateLimitStorage) {
        this.propertiesHelper = propertiesHelper;
        this.blackListStorage = blackListStorage;
        this.rateLimitStorage = rateLimitStorage;
    }

    protected boolean preHandle(HttpServerExchange exchange, ServiceCode serviceCode) {
        // 第一步，验证是否在黑名单
        HeaderMap headers = exchange.getRequestHeaders();
        String ipAddress = IpUtils.getIpAddress(exchange);
        if(blackListStorage.isForbidden(ipAddress)){
            sendErrorMsg(exchange,ErrorCode.REQUEST_FORBIDDEN, null);
            return false;
        }

        // 第二步，验证身份
        if (serviceCode.needAuth) {
            String token = headers.getFirst("token");
            if (!JwtUtils.verifyToken(token)) {
                sendErrorMsg(exchange,ErrorCode.UN_AUTHED, null);
                return false;
            }
        }

        // 第三步，验证是否限流
        List<RateLimit> rateLimits = propertiesHelper.getRateLimits(serviceCode);
        // 未做限流配置，直接放行
        if (CollectionUtils.isEmpty(rateLimits)) {
            return true;
        }
        // 按限流级别分别验证
        for (RateLimit rateLimit : rateLimits) {
            String key = makeKey(rateLimit, exchange);
            long i = rateLimitStorage.incrementAndGet(key);
            if (i == 1) {
                rateLimitStorage.setExpiredAt(key, rateLimit.getInterval(), rateLimit.getTimeUnit());
            }
            // 被限流
            if (i > rateLimit.getLimitCount()) {
                sendErrorMsg(exchange,ErrorCode.RATE_LIMIT, null);
                return false;
            }
        }
        return true;
    }

    private String makeKey(RateLimit rateLimit, HttpServerExchange exchange) {
        switch (rateLimit.getLimitType()) {
            case API:
                return exchange.getRequestPath();
            case IP:
                return IpUtils.getIpAddress(exchange);
            case USER:
                return JwtUtils.getUid(exchange.getRequestHeaders().getFirst("token"));
            case SERVICE:
            default:
                return rateLimit.getServiceCode().name();
        }
    }

    /**
     * 请求路径匹配服务代码
     *
     * @param exchange exchange
     * @return 服务代码
     */
    protected ServiceCode getServiceCode(HttpServerExchange exchange) {
        String requestPath = exchange.getRequestPath();
        for (ServiceCode serviceCode : ServiceCode.values()) {
            if (requestPath.startsWith(serviceCode.contextPath)) {
                return serviceCode;
            }
        }
        return null;
    }

    /**
     * 负载均衡
     *
     * @param serviceCode 服务代码
     * @return 某个服务
     */
    protected ServiceInfo getServiceInfo(ServiceCode serviceCode) {
        return propertiesHelper.getServiceInfo(serviceCode);
    }

    public void sendErrorMsg(HttpServerExchange exchange,ErrorCode errorCode,Object errorInfo){
        HttpUtils.sendErrorResponse(exchange, errorCode, errorInfo);
    }
}
