package com.simon.ocean;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhouzhenyong
 * @since 2017/6/12.
 */
@UtilityClass
public class StringTypeUtil {

    /**
     * 数组toString的字符串转换过来
     */
    public List strToList(String string) {
        string = string.replace("[", "").replace("]", "").replace(" ", "");
        List<String> resultList = Arrays.asList(string.split(","));
        if (StringUtils.isBlank(resultList.get(0)) && resultList.size() == 1) {
            return null;
        }
        return resultList;
    }

    /**
     * 是否包含字母
     */
    public boolean haveStr(String content) {
        String regex = ".*[a-zA-Z]+.*";
        Matcher m = Pattern.compile(regex).matcher(content);
        return m.matches();
    }

    /**
     * 全是字母
     */
    public boolean allStr(String content) {
        String regex = "^[A-Za-z]+$";
        Matcher m = Pattern.compile(regex).matcher(content);
        return m.matches();
    }

    /**
     * 全是数字
     */
    public boolean allNumber(String content) {
        String regex = "^[0-9]*$";
        Matcher m = Pattern.compile(regex).matcher(content);
        return m.matches();
    }

    /**
     * 含有数字
     */
    public boolean haveNumber(String content) {
        String regex = "^.*[0-9]+.*$";
        Matcher m = Pattern.compile(regex).matcher(content);
        return m.matches();
    }

    /**
     * 含有中文
     */
    public boolean haveChinaWord(String content) {
        String regex = "^.*[\\u4e00-\\u9fa5]+.*$";
        Matcher m = Pattern.compile(regex).matcher(content);
        return m.matches();
    }
}
