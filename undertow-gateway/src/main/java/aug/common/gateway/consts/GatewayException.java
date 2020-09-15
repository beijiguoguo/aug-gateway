package aug.common.gateway.consts;

/**
 * @author guoxiaoyong
 * @date 2020/9/10
 */
public class GatewayException extends RuntimeException {
    public ErrorCode errorCode;

    public GatewayException(ErrorCode errorCode) {
        super(errorCode.msg, null, false, false);
        this.errorCode = errorCode;
    }
}
