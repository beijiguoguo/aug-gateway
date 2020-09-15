package aug.common.gateway.db.dao;

import aug.common.gateway.db.po.ServiceInfo;
import org.jooq.DSLContext;
import org.jooq.Field;

import java.util.List;

import static aug.common.gateway.db.jooq.Tables.GW_SERVICE;

/**
 * @author guoxiaoyong
 * @date 2020/9/10
 */
public class ServiceDAO {

    private final DBConnector dbConnector;

    public ServiceDAO(DBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    private final Field<Integer> id = GW_SERVICE.ID;
    private final Field<String> serviceCode = GW_SERVICE.SERVICE_CODE;
    private final Field<Integer> serviceNum = GW_SERVICE.SERVICE_NUM;
    private final Field<String> serviceName = GW_SERVICE.SERVICE_NAME;
    private final Field<String> host = GW_SERVICE.HOST;
    private final Field<Integer> port = GW_SERVICE.PORT;
    private final Field<String> status = GW_SERVICE.STATUS;

    private DSLContext dslContext() {
        return dbConnector.dslContext();
    }

    public List<ServiceInfo> selectAll() {
        return dslContext()
                .select(
                        id,
                        serviceCode,
                        serviceNum,
                        serviceName,
                        host,
                        port,
                        status
                )
                .from(GW_SERVICE)
                .fetchInto(ServiceInfo.class);
    }
}
