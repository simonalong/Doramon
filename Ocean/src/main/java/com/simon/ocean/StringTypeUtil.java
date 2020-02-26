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
     * 专门用于解析json对应的string到界面回车的显示，即反向解压json
     */
    public String parseJson(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean spaceFlag = false;
        //[]这个括号内部的逗号不替换
        boolean leftFlag = false;
        char[] charList = str.toCharArray();
        int leftCount = 0;
        for (char c : charList) {
            if (c == ',') {
                if (leftFlag) {
                    stringBuilder.append(c);
                    continue;      //表示当前处于是数组中的逗号，这个时候不关心
                }
                stringBuilder.append(",\n");
                if (spaceFlag) {
                    stringBuilder.append(addSpace(leftCount));
                }
            } else if (c == '{') {
                stringBuilder.append("\n");
                if (spaceFlag) {
                    stringBuilder.append(addSpace(leftCount));
                }
                spaceFlag = true;
                stringBuilder.append("{\n");
                leftCount++;
                stringBuilder.append(addSpace(leftCount));
            } else if (c == '}') {
                leftCount--;
                stringBuilder.append("\n");
                if (spaceFlag) {
                    stringBuilder.append(addSpace(leftCount));
                }
                stringBuilder.append("}");
            } else if (c == '[') {
                leftFlag = true;
                stringBuilder.append(c);
            } else if (c == ']') {
                leftFlag = false;
                stringBuilder.append(c);
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    private String addSpace(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            stringBuilder.append("     ");
        }
        return stringBuilder.toString();
    }

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
