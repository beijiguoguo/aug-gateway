package aug.common.gateway.utils;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;

import java.net.SocketAddress;

/**
 * ip工具
 *
 * @author guoxiaoyong
 * @date 2020/9/10
 */
public class IpUtils {

    private static final String UNKNOWN = "unknown";

    /**
     * 获取Ip地址
     */
    public static String getIpAddress(HttpServerExchange exchange) {
        HeaderMap headers = exchange.getRequestHeaders();
        String ip = headers.getFirst("x-real-ip");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Forwarded-For");
            if (ip != null) {
                ip = ip.split(",")[0].trim();
            }
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip)) {
            SocketAddress peerAddress = exchange.getConnection().getPeerAddress();
            if (peerAddress != null) {
                String s = peerAddress.toString();
                String substring = s.substring(1);
                ip = substring.substring(0, substring.lastIndexOf(":"));
            }
        }
        return ip;
    }
}
