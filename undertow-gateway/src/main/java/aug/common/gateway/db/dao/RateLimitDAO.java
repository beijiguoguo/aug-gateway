package aug.common.gateway.db.dao;

import aug.common.gateway.db.po.RateLimit;
import org.jooq.DSLContext;
import org.jooq.Field;

import java.util.List;

import static aug.common.gateway.db.jooq.Tables.GW_RATE_LIMIT;

/**
 * @author guoxiaoyong
 * @date 2020/9/10
 */
public class RateLimitDAO {
    private final DBConnector dbConnector;

    public RateLimitDAO(DBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    private final Field<Integer> id = GW_RATE_LIMIT.ID;
    private final Field<String> serviceCode = GW_RATE_LIMIT.SERVICE_CODE;
    private final Field<String> limitType = GW_RATE_LIMIT.LIMIT_TYPE;
    private final Field<Long> limitCount = GW_RATE_LIMIT.LIMIT_COUNT;
    private final Field<Long> interval = GW_RATE_LIMIT.INTERVAL;
    private final Field<String> timeUnit = GW_RATE_LIMIT.TIME_UNIT;

    private DSLContext dslContext() {
        return dbConnector.dslContext();
    }

    public List<RateLimit> selectAll() {
        return dslContext()
                .select(
                        id,
                        serviceCode,
                        limitType,
                        limitCount,
                        interval,
                        timeUnit
                )
                .from(GW_RATE_LIMIT)
                .fetchInto(RateLimit.class);
    }
}
