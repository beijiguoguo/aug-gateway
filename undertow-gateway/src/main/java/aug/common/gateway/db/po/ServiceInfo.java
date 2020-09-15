package aug.common.gateway.db.po;

import aug.common.gateway.consts.ServiceCode;

/**
 * 服务配置信息
 * @author guoxiaoyong
 * @date 2020/9/8
 */
public class ServiceInfo {

    /**
     * 服务状态
     */
    public enum ServiceStatus {
        // 在线
        ONLINE,
        // 离线
        OFFLINE
    }

    private Integer id;
    /**
     * 服务代码
     */
    private ServiceCode serviceCode;
    /**
     * 服务序号
     */
    private Integer serviceNum;
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 域名或ip
     */
    private String host;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 服务状态
     */
    private ServiceStatus status;

    public Integer getId() {
        return id;
    }

    public ServiceInfo setId(Integer id) {
        this.id = id;
        return this;
    }

    public ServiceCode getServiceCode() {
        return serviceCode;
    }

    public ServiceInfo setServiceCode(ServiceCode serviceCode) {
        this.serviceCode = serviceCode;
        return this;
    }

    public Integer getServiceNum() {
        return serviceNum;
    }

    public ServiceInfo setServiceNum(Integer serviceNum) {
        this.serviceNum = serviceNum;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ServiceInfo setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getHost() {
        return host;
    }

    public ServiceInfo setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public ServiceInfo setPort(Integer port) {
        this.port = port;
        return this;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public ServiceInfo setStatus(ServiceStatus status) {
        this.status = status;
        return this;
    }
}
