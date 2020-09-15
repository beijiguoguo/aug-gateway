package aug.common.gateway.consts;

/**
 * @author guoxiaoyong
 * @date 2020/9/10
 */
public interface RequestWarning {
    GatewayException UN_AUTHED = new GatewayException(ErrorCode.UN_AUTHED);
}
