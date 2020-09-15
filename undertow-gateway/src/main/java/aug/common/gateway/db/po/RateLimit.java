package aug.common.gateway.db.po;

import aug.common.gateway.consts.ServiceCode;

import java.util.concurrent.TimeUnit;

/**
 * 限流配置信息
 *
 * @author guoxiaoyong
 * @date 2020/9/9
 */
public class RateLimit {
    /**
     * 限流类型
     */
    public enum LimitType {
        /**
         * 服务
         */
        SERVICE(1),
        /**
         * 接口
         */
        API(2),
        /**
         * IP
         */
        IP(3),
        /**
         * 用户
         */
        USER(4);

        public final int level;

        LimitType(int level) {
            this.level = level;
        }

    }

    private Integer id;
    /**
     * 服务代码
     */
    private ServiceCode serviceCode;
    /**
     * 限流类型
     */
    private LimitType limitType;
    /**
     * 最大允许次数
     */
    private Long limitCount;
    /**
     * 时间间隔
     */
    private Long interval;
    /**
     * 时间单位
     */
    private TimeUnit timeUnit;

    public Integer getId() {
        return id;
    }

    public RateLimit setId(Integer id) {
        this.id = id;
        return this;
    }

    public ServiceCode getServiceCode() {
        return serviceCode;
    }

    public RateLimit setServiceCode(ServiceCode serviceCode) {
        this.serviceCode = serviceCode;
        return this;
    }

    public LimitType getLimitType() {
        return limitType;
    }

    public RateLimit setLimitType(LimitType limitType) {
        this.limitType = limitType;
        return this;
    }

    public Long getLimitCount() {
        return limitCount;
    }

    public RateLimit setLimitCount(Long limitCount) {
        this.limitCount = limitCount;
        return this;
    }

    public Long getInterval() {
        return interval;
    }

    public RateLimit setInterval(Long interval) {
        this.interval = interval;
        return this;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public RateLimit setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

}
