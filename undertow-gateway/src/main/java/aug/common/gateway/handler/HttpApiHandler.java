package aug.common.gateway.handler;

import aug.common.gateway.consts.ErrorCode;
import aug.common.gateway.consts.ServiceCode;
import aug.common.gateway.db.po.ServiceInfo;
import aug.common.gateway.helper.BlackListStorage;
import aug.common.gateway.helper.PropertiesHelper;
import aug.common.gateway.helper.RateLimitStorage;
import aug.common.gateway.utils.HttpUtils;
import io.undertow.server.HttpServerExchange;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Http通用请求处理器
 *
 * @author guoxiaoyong
 * @date 2020/8/14
 */
public class HttpApiHandler extends AbstractHttpHandler {

    public HttpApiHandler(PropertiesHelper propertiesHelper, BlackListStorage blackListStorage, RateLimitStorage rateLimitStorage) {
        super(propertiesHelper, blackListStorage, rateLimitStorage);
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) {
        httpServerExchange
                .getRequestReceiver()
                .receiveFullString((exchange, s) -> {
                    try {
                        ServiceCode serviceCode = getServiceCode(exchange);
                        if (serviceCode == null) {
                            HttpUtils.send404Error(exchange);
                            return;
                        }

                        boolean preHandled = preHandle(exchange, serviceCode);
                        if (!preHandled) {
                            return;
                        }

                        ServiceInfo serviceInfo = getServiceInfo(serviceCode);
                        if (serviceInfo == null) {
                            // 该服务集群全部离线
                            sendErrorMsg(exchange, ErrorCode.SERVICE_UNAVAILABLE, null);
                            // TODO 应当发送警告
                            return;
                        }

                        HttpUrl httpUrl = HttpUtils.generateHttpUrl(exchange, serviceInfo, serviceCode);
                        Request request = HttpUtils.generateRequest(exchange, httpUrl, s);

                        Response response = HttpUtils.proxyRequest(request);
                        HttpUtils.sendOriginalResponse(exchange, response);
                    } catch (IOException e) {
                        sendErrorMsg(exchange, ErrorCode.SERVICE_UNAVAILABLE, e.getLocalizedMessage());
                    } catch (Exception e) {
                        sendErrorMsg(exchange, ErrorCode.SERVER_ERROR, e.getLocalizedMessage());
                    }
                }, (httpServerExchange12, e) -> {
                }, StandardCharsets.UTF_8);
    }

}
