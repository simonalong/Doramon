package ${appPath}.config;

import com.simon.neo.Neo;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author robot
 */
@Configuration
@EnableConfigurationProperties(DbProperties.class)
public class DbConfiguration {

    @Bean
    public Neo shareShop(DbProperties db){
        return Neo.connect(db.getUrl(), db.getUsername(), db.getPassword());
    }
}