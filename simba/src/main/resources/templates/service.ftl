package ${appPath}.service;

import ${appPath}.dao.BaseDao;
import ${appPath}.dao.${tablePathName}Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author robot
 */
@Service
public class ${tablePathName}Service extends BaseService{

    @Autowired
    private ${tablePathName}Dao ${tablePathNameLower}Dao;

    @Override
    protected BaseDao getDao() {
        return ${tablePathNameLower}Dao;
    }
}
