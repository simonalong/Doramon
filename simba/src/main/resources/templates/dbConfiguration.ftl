package ${appPath}.config;

import ${idGenPath};
import me.zzp.am.Ao;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author robot
 */
@Configuration
@EnableConfigurationProperties(DbProperties.class)
public class DbConfiguration {

    @Bean
    public Ao ${dbName}(DbProperties db){
        return Ao.open(db.getUrl(), db.getUsername(), db.getPassword());
    }

    @Bean
    public IdGeneratorServer idGeneratorServer(@Qualifier("dataSource") DataSource dataSource){
        return new IdGeneratorServer(dataSource);
    }
}
