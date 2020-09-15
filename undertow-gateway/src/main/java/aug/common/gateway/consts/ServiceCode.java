package aug.common.gateway.consts;

/**
 * 服务代码配置信息
 *
 * @author guoxiaoyong
 * @date 2020/9/8
 */
public enum ServiceCode {

    /**
     * sso服务
     */
    SSO("/api/sso/", "", true);

    /**
     * 路径前缀
     */
    public final String contextPath;
    /**
     * 替换内容
     */
    public final String replacement;
    /**
     * 是否需要认证
     */
    public final boolean needAuth;

    ServiceCode(String contextPath, String replacement, boolean needAuth) {
        this.contextPath = contextPath;
        this.replacement = replacement;
        this.needAuth = needAuth;
    }

}
