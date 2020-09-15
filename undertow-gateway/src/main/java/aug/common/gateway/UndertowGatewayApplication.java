package aug.common.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;

/**
 * @author guoxiaoyong
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, JooqAutoConfiguration.class})
public class UndertowGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(UndertowGatewayApplication.class, args);
    }

}
