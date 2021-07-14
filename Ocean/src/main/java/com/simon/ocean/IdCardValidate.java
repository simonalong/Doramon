package com.simon.ocean;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author shizi
 * @since 2021-07-14 22:02:19
 */
@UtilityClass
public class IdCardValidate {

    // 加权因子
    private final int[] weightFactor = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    // 校验码
    private final char[] checkCode = new char[]{'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    // 第一位不可能是0
    // 第二位到第六位可以是0-9
    // 第七位到第十位是年份，所以七八位为19或者20
    // 十一位和十二位是月份，这两位是01-12之间的数值
    // 十三位和十四位是日期，是从01-31之间的数值
    // 十五，十六，十七都是数字0-9
    // 十八位可能是数字0-9，也可能是X
    private final String idCardPatter = "^[1-9][0-9]{5}([1][9][0-9]{2}|[2][0][0|1][0-9])([0][1-9]|[1][0|1|2])([0][1-9]|[1|2][0-9]|[3][0|1])[0-9]{3}([0-9]|[X])$";
    private final int idCardSize = 17;

    /**
     * 验证身份证号是否合法
     */
    public boolean isValidate(String idCard) {
        if (null == idCard || "".equals(idCard)) {
            return false;
        }

        if (idCard.length() < idCardSize) {
            return false;
        }

        String seventeen = idCard.substring(0, idCardSize);

        List<Integer> dataList = Arrays.stream(seventeen.split("")).map(Integer::valueOf).collect(Collectors.toList());
        int num = 0;
        for (int index = 0; index < dataList.size(); index++) {
            num += (dataList.get(index) * weightFactor[index]);
        }

        // 判断最后一位校验码是否正确
        // 返回验证结果，校验码和格式同时正确才算是合法的身份证号码
        return idCard.charAt(idCard.length() - 1) == checkCode[num % 11] && Pattern.compile(idCardPatter).matcher(idCard).matches();
    }
}
