package ${appPath}.controller;

import ${appPath}.constants.AdminConstant;
import ${appPath}.service.${tablePathName}Service;
import com.simon.neo.NeoMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

/**
 * @author robot
 */
@Slf4j
@RestController
@RequestMapping(AdminConstant.ADMIN_API_V1 + "/" + "${tableUrlName}")
public class ${tablePathName}Controller extends BaseResponseController {

    @Autowired
    private ${tablePathName}Service ${tablePathNameLower}Service;

    @PutMapping("add")
    public ResponseEntity add(@RequestBody NeoMap record, HttpServletRequest httpServletRequest) {
        log.debug("增加：" + record);
        try {
            return ok(${tablePathNameLower}Service.insert(record));
        } catch (Exception e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Integer> delete(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        log.debug("删除：" + id);
        try {
            return ok(${tablePathNameLower}Service.delete(id));
        } catch (Exception e) {
            e.printStackTrace();
            return error(0);
        }
    }

    @PostMapping("update")
    public ResponseEntity update(@RequestBody NeoMap record, HttpServletRequest httpServletRequest) {
        log.debug("更新：" + record);
        try {
            return ok(${tablePathNameLower}Service.update(record));
        } catch (Exception e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
    }

    @PostMapping("pageList")
    public ResponseEntity<List> pageList(@RequestBody NeoMap record, HttpServletRequest httpServletRequest) {
        log.debug("查看分页数据：" + record);
        return ok(${tablePathNameLower}Service.getPage(record));
    }

    @PostMapping("count")
    public ResponseEntity<Integer> count(@RequestBody NeoMap record, HttpServletRequest httpServletRequest) {
        log.debug("查看总个数：" + record);
        return ok(${tablePathNameLower}Service.count(record));
    }
}
