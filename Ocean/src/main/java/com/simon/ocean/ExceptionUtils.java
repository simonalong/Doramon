package com.simon.ocean;

import lombok.experimental.UtilityClass;

/**
 * @author zhouzhenyong
 * @since 2018/8/3 下午3:39
 */
@UtilityClass
public class ExceptionUtils {

    /**
     * Finds throwing cause in exception stack. Returns throwable object if cause class is matched. Otherwise, returns
     * <code>null</code>.
     */
    @SuppressWarnings({"unchecked"})
    public <T extends Throwable> T findCause(Throwable throwable, Class<T> cause) {
        while (throwable != null) {
            if (throwable.getClass().equals(cause)) {
                return (T) throwable;
            }
            throwable = throwable.getCause();
        }
        return null;
    }
}
