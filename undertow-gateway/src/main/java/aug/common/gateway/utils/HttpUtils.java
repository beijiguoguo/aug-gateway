package aug.common.gateway.utils;

import aug.common.gateway.consts.ErrorCode;
import aug.common.gateway.consts.ServiceCode;
import aug.common.gateway.db.po.ServiceInfo;
import aug.common.gateway.server.CommonResponse;
import io.reactivex.processors.PublishProcessor;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;
import okhttp3.*;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Http工具
 *
 * @author guoxiaoyong
 * @date 2020/9/9
 */
public class HttpUtils {

    private static final String MEDIA_TYPE = "application/json; charset=utf-8";
    private static final String NO_CACHE = "no-cache";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse(MEDIA_TYPE);

    public final static HttpString CONTENT_TYPE = HttpString.tryFromString("Content-Type");
    public final static HttpString CACHE_CONTROL = HttpString.tryFromString("Cache-Control");
    public final static HttpString METHOD_GET = HttpString.tryFromString("GET");
    public final static HttpString METHOD_POST = HttpString.tryFromString("POST");

    private static final OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .connectionPool(new ConnectionPool())
                    .build();

    private static final PublishProcessor<String> PROCESSOR = PublishProcessor.create();

    public static PublishProcessor<String> getProcessor() {
        return PROCESSOR;
    }

    public static HttpUrl.Builder generateHttpUrlBuilder(HttpServerExchange exchange, @NonNull ServiceInfo serviceInfo) {
        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme("http")
                .host(serviceInfo.getHost())
                .port(serviceInfo.getPort());

        Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
        if (!CollectionUtils.isEmpty(queryParameters)) {
            for (Map.Entry<String, Deque<String>> dequeEntry : queryParameters.entrySet()) {
                for (String s1 : dequeEntry.getValue()) {
                    builder.addQueryParameter(dequeEntry.getKey(), s1);
                }
            }
        }
        return builder;
    }

    public static Request generateRequest(HttpServerExchange exchange, ServiceCode serviceCode, HttpUrl.Builder builder, String bodyString) {
        String requestPath =
                exchange.getRequestPath()
                        .replace(serviceCode.contextPath, serviceCode.replacement);

        Request.Builder requestBuilder =
                new Request.Builder()
                        .url(
                                builder.addPathSegments(requestPath).build()
                        );

        HttpString requestMethod = exchange.getRequestMethod();
        if (METHOD_GET.equals(requestMethod)) {
            requestBuilder = requestBuilder.get();
        } else if (METHOD_POST.equals(requestMethod)) {
            requestBuilder = requestBuilder.post(RequestBody.create(MEDIA_TYPE_JSON, bodyString));
        } else {
            throw new RuntimeException("Method not support!");
        }

        Headers.Builder headersBuilder = new Headers.Builder();
        for (HeaderValues headerValues : exchange.getRequestHeaders()) {
            HttpString headerName = headerValues.getHeaderName();
            String headerValue = headerValues.getFirst();
            headersBuilder.add(headerName.toString(), headerValue);
        }

        return requestBuilder
                .headers(headersBuilder.build())
                .build();
    }

    public static Response proxyRequest(Request request) throws IOException {
        return HTTP_CLIENT.newCall(request).execute();
    }

    public static void sendOriginalResponse(HttpServerExchange exchange, Response response) throws IOException {
        ResponseBody body = response.body();
        if (body != null) {
            sendResponse(exchange, response.code(), body.string());
        }
    }

    public static void sendErrorResponse(HttpServerExchange exchange, ErrorCode errorCode, Object errorInfo) {
        sendResponse(
                exchange,
                errorCode.code,
                JsonUtils.toJsonStringPretty(
                        CommonResponse.makeResponse(errorCode.code, errorCode.msg, errorInfo)
                ));
        // 已加入黑名单，不必再重复处理，避免不必要的io操作
        if (!ErrorCode.REQUEST_FORBIDDEN.equals(errorCode)) {
            PROCESSOR.onNext(IpUtils.getIpAddress(exchange));
        }
    }

    public static void send404Error(HttpServerExchange exchange) {
        sendErrorResponse(exchange, ErrorCode.PATH_NOT_FOUND, exchange.getRequestPath());
    }

    private static void sendResponse(HttpServerExchange exchange, int code, String response) {
        exchange.setStatusCode(code);

        HeaderMap responseHeaders = exchange.getResponseHeaders();
        responseHeaders.put(CONTENT_TYPE, MEDIA_TYPE);
        responseHeaders.put(CACHE_CONTROL, NO_CACHE);

        exchange.getResponseSender().send(response);
    }

}
