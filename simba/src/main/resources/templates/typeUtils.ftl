package ${appPath}.util;

import lombok.experimental.UtilityClass;

/**
 * @author robot
 */
@UtilityClass
public class TypeUtils {

    /**
     * 判断一个类型是否可以转换为某种类型
     */
    public boolean canType(String obj, Class cls){
        try {
            cls.getMethod("valueOf", String.class).invoke(null, obj);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
