package ${appPath}.view;

import lombok.Builder;
import lombok.Data;

/**
 * @author robot
 */
@Data
@Builder
public class LoginResponseEntity {

    private String status;
    private String type;
    private String currentAuthority;

    public static LoginResponseEntity build(AccountEntity account){
        if(null != account) {
            return LoginResponseEntity.builder().type(account.getType()).status("ok")
                .currentAuthority(account.getAuthority()).build();
        }
        return LoginResponseEntity.builder().build();
    }

    public static LoginResponseEntity loginFail(){
        return LoginResponseEntity.builder().status("fail").build();
    }
}
