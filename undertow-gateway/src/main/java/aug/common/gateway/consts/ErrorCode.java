package aug.common.gateway.consts;

/**
 * 错误码
 *
 * @author guoxiaoyong
 * @date 2020/9/9
 */
public enum ErrorCode {
    /**
     * 参数异常
     */
    ILLEGAL_ARGUMENT(400, "参数异常"),
    /**
     * 非法请求，参数异常
     */
    UN_AUTHED(401, "身份认证失败"),
    /**
     * 拒绝请求，可能已被加入黑名单
     */
    REQUEST_FORBIDDEN(403, "请求拒绝"),
    /**
     * 请求的路径不存在
     */
    PATH_NOT_FOUND(404, "路径不存在"),
    /**
     * 请求触发限流
     */
    RATE_LIMIT(429, "超过限流次数"),
    /**
     * 网关服务异常
     */
    SERVER_ERROR(500, "网关服务异常"),
    /**
     * 请求的服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    /**
     * 网关超时
     */
    SERVICE_TIMEOUT(504, "网关超时");

    public final int code;
    public final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
