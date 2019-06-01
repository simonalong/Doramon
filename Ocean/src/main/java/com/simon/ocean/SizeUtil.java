package com.simon.ocean;

import lombok.experimental.UtilityClass;
import org.apache.lucene.util.RamUsageEstimator;

/**
 * @author zhouzhenyong
 * @since 2019/2/22 下午3:13
 */
@UtilityClass
public class SizeUtil {

    public long sizeOf(Object object){
        return RamUsageEstimator.sizeOf(object);
    }

    /**
     * 数据尺寸直接显示尺寸的大小，Byte, KB, MB, GB
     */
    public String strSizeOf(Object object){
        return RamUsageEstimator.humanReadableUnits(object);
    }

    public String strSizeOf(Integer num, Object object){
        return RamUsageEstimator.humanReadableUnits(num * sizeOf(object));
    }
}
