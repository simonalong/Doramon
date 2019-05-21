package ${appPath}.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author robot
 */
@Getter
@Setter
@ConfigurationProperties("spring.datasource")
public class DbProperties {
    private String url;
    private String username;
    private String password;
}
