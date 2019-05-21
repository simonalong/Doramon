package com.simon.simba;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

/**
 * @author zhouzhenyong
 * @since 2019/1/3 上午11:51
 */
@UtilityClass
public class ListUtils {

    /**
     * list 分段，每一段数据大小
     */
    public List<List> split(List dataList, Integer splitNum) {
        if (null == dataList || dataList.isEmpty() || splitNum == 0) {
            return new ArrayList();
        }

        List<List> splitList = new ArrayList<>();
        Integer size = dataList.size();

        while ((size / splitNum) != 0) {
            splitList.add(dataList.subList(0, splitNum));
            dataList = dataList.subList(splitNum, size);
            size = dataList.size();
        }

        if (0 != size) {
            splitList.add(dataList.subList(0, size));
        }
        return splitList;
    }
}
