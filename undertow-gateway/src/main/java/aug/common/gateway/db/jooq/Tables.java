/*
 * This file is generated by jOOQ.
*/
package aug.common.gateway.db.jooq;


import aug.common.gateway.db.jooq.tables.GwRateLimit;
import aug.common.gateway.db.jooq.tables.GwService;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in aug_gateway
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * 限流配置表
     */
    public static final GwRateLimit GW_RATE_LIMIT = aug.common.gateway.db.jooq.tables.GwRateLimit.GW_RATE_LIMIT;

    /**
     * 服务配置表
     */
    public static final GwService GW_SERVICE = aug.common.gateway.db.jooq.tables.GwService.GW_SERVICE;
}