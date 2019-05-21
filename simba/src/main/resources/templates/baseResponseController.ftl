package ${appPath}.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author robot
 */
public abstract class BaseResponseController {
    public <T> ResponseEntity<T> ok(T body){
        return ResponseEntity.ok().body(body);
    }

    public <T> ResponseEntity<T> error(T body){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
