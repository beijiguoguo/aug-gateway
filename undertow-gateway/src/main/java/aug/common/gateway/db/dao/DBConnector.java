package aug.common.gateway.db.dao;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.boot.autoconfigure.jooq.SpringTransactionProvider;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

/**
 * @author guoxiaoyong
 * @date 2020/9/10
 */
public class DBConnector {
    private final DataSourceTransactionManager transactionManager;

    private final DSLContext dslContext;

    public DBConnector(DataSource dataSource) {
        this.transactionManager = createDataSourceTransactionManager(dataSource);
        this.dslContext = createDSLContext(dataSource, transactionManager);
    }

    private DataSourceTransactionManager createDataSourceTransactionManager(
            DataSource dataSource
    ) {
        return new DataSourceTransactionManager(dataSource);
    }

    private static DefaultDSLContext createDSLContext(DataSource dataSource, PlatformTransactionManager txManager) {
        System.getProperties().setProperty("org.jooq.no-logo", "true");
        DataSourceConnectionProvider dataSourceConnectionProvider
                = new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
        SpringTransactionProvider transactionProvider
                = new SpringTransactionProvider(txManager);
        DefaultExecuteListenerProvider defaultExecuteListenerProvider
                = new DefaultExecuteListenerProvider(new JooqExceptionTranslator());
        DefaultConfiguration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.MYSQL);
        configuration.set(dataSourceConnectionProvider);
        configuration.set(transactionProvider);
        configuration.set(defaultExecuteListenerProvider);
        configuration.settings().withRenderSchema(false);
        return new DefaultDSLContext(configuration);
    }

    public DSLContext dslContext() {
        return dslContext;
    }

    public void runTransaction(Runnable runnable) {
        runTransaction(runnable, transactionManager);
    }

    private static void runTransaction(Runnable runnable, DataSourceTransactionManager transactionManager) {
        TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            runnable.run();
            transactionManager.commit(tx);
        } catch (Exception e) {
            transactionManager.rollback(tx);
            throw e;
        }
    }

    public void destroy() {
        dslContext.close();
    }
}
