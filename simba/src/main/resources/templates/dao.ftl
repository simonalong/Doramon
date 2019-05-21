package ${appPath}.dao;

import org.springframework.stereotype.Repository;

/**
 * @author robot
 */
@Repository
public class ${tablePathName}Dao extends BaseDao{

    private static final String TABLE_NAME = "${tableName}";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
