package com.simon.doraemon.controller;

import com.simon.doraemon.constants.AdminConstant;
import com.simon.doraemon.service.ConfigGroupService;
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
@RequestMapping(AdminConstant.ADMIN_API_V1 + "/" + "config_group")
public class ConfigGroupController extends BaseResponseController {

    @Autowired
    private ConfigGroupService configGroupService;

    @PutMapping("add")
    public ResponseEntity add(@RequestBody NeoMap record) {
        log.debug("增加：" + record);
        try {
            return ok(configGroupService.insert(record));
        } catch (Exception e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Integer> delete(@PathVariable Long id) {
        log.debug("删除：" + id);
        try {
            return ok(configGroupService.delete(id));
        } catch (Exception e) {
            e.printStackTrace();
            return error(0);
        }
    }

    @PostMapping("update")
    public ResponseEntity update(@RequestBody NeoMap record) {
        log.debug("更新：" + record);
        try {
            return ok(configGroupService.update(record));
        } catch (Exception e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
    }

    @PostMapping("pageList")
    public ResponseEntity<List> pageList(@RequestBody NeoMap record) {
        log.debug("查看分页数据：" + record);
        return ok(configGroupService.getPage(record));
    }

    @PostMapping("count")
    public ResponseEntity<Integer> count(@RequestBody NeoMap record) {
        log.debug("查看总个数：" + record);
        return ok(configGroupService.count(record));
    }
}
