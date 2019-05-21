package com.simon.simba;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhouzhenyong
 * @since 2019/1/2 下午5:02
 */
public class Strings {

    /**
     * 返回指定字符串中匹配执行正则表达式的子字符串序列。
     *
     * @param s 待处理的字符串。
     * @param re 需要查找的正则表达式
     * @return 返回 s 中匹配上 re 的字符串序列。
     */
    public static List<String> reSeq(String s, String re) {
        List<String> list = new LinkedList<>();

        Matcher matcher = Pattern.compile(re).matcher(s);
        while (matcher.find()) {
            list.add(matcher.group());
        }

        return list;
    }

    /**
     * 返回指定字符串中指定字符出现的次数。
     *
     * @param s 待处理的字符串。
     * @param c 待查找的字符。
     * @return 返回 s 中出现 c 的次数。
     */
    public static int countOccurrencesOf(String s, char c) {
        int count = 0;
        for (int index = -1; (index = s.indexOf(c, index + 1)) >= 0; count++){}
        return count;
    }

    static String identity(String s) {
        return s;
    }

    /**
     * 将驼峰转换成非严格的下划线风格。
     *
     * 遇到连续的大写字母，仅在第一个大写字母前添加下划线。 例如 UserID 转换成 user_id。
     *
     * @param s 待转换字符串
     * @return 如果输入是null，则返回null，否则返回转换后的字符串
     */
    public static String toUnderScore(String s) {
        return s == null ? null : s.replaceAll("(?<=[^A-Z])[A-Z]", "_$0").toLowerCase();
    }

    /**
     * 将驼峰转换成严格的下划线风格。
     *
     * 遇到连续的大写字母，在每个大写字母前添加下划线。 例如 UserID 转换成 user_i_d。
     *
     * @param s 待转换字符串
     * @return 如果输入是null，则返回null，否则返回转换后的字符串
     */
    public static String toUnderScoreStrict(String s) {
        return s == null ? null : s.replaceAll("\\B[A-Z]", "_$0").toLowerCase();
    }

    private final static Pattern patternUnderScore = Pattern.compile("(?<=_)[a-zA-Z]");
    private final static Pattern patternUnderScoreAll = Pattern.compile("(?<=^|_)[a-zA-Z]");

    private static String toCamelCase(Pattern pattern, String s) {
        if (s == null) {
            return null;
        }

        StringBuffer name = new StringBuffer();

        Matcher m = pattern.matcher(s);
        while (m.find()) {
            m.appendReplacement(name, m.group().toUpperCase());
        }
        m.appendTail(name);

        return name.toString().replaceAll("_+", "");
    }

    /**
     * 非严格的驼峰风格，仅将下划线后的字母大写化，其他字母保持不变。
     *
     * 例如 user_iD 转换成改成 userID。
     *
     * @param s 待转换字符串
     * @return 如果输入是null，则返回null，否则返回转换后的字符串
     */
    public static String toCamelCase(String s) {
        return toCamelCase(patternUnderScore, s);
    }

    /**
     * 严格的驼峰风格，除了将下划线后的字母大写化，其他字母全部小写。
     *
     * 例如 user_iD 转换成改成 userId。
     *
     * @param s 待转换字符串
     * @return 如果输入是null，则返回null，否则返回转换后的字符串
     */
    public static String toCamelCaseStrict(String s) {
        return toCamelCase(patternUnderScore, s == null ? null : s.toLowerCase());
    }

    /**
     * 非严格的驼峰风格，仅将开头的或下划线后的字母大写化，其他字母保持不变。
     *
     * 例如 user_iD 转换成改成 UserID。
     *
     * @param s 待转换字符串
     * @return 如果输入是null，则返回null，否则返回转换后的字符串
     */
    public static String toCamelCaseAll(String s) {
        return toCamelCase(patternUnderScoreAll, s);
    }

    /**
     * 严格的驼峰风格，除了将开头或下划线后的字母大写化，其他字母全部小写。
     *
     * 例如 user_iD 转换成改成 UserId。
     *
     * @param s 待转换字符串
     * @return 如果输入是null，则返回null，否则返回转换后的字符串
     */
    public static String toCamelCaseAllStrict(String s) {
        return toCamelCase(patternUnderScoreAll, s == null ? null : s.toLowerCase());
    }

    /**
     * 转成小写字母。
     *
     * @param s 待转换字符串
     * @return 如果输入是null，这返回null，否则返回转换后的字符串
     */
    public static String toLowerCase(String s) {
        return s != null? s.toLowerCase(): null;
    }

    /**
     * 仅保留转换后的小写字母。
     *
     * @param s 待转换字符串
     * @return 如果输入是null，这返回null，否则返回转换后的字符串
     */
    public static String toLowerCaseOnly(String s) {
        return s != null? s.toLowerCase().replaceAll("[^0-9a-z]+", ""): null;
    }

    /**
     * 转成大写字母。
     *
     * @param s 待转换字符串
     * @return 如果输入是null，这返回null，否则返回转换后的字符串
     */
    public static String toUpperCase(String s) {
        return s != null? s.toUpperCase(): null;
    }

    /**
     * 仅保留转换后的大写字母。
     *
     * @param s 待转换字符串
     * @return 如果输入是null，这返回null，否则返回转换后的字符串
     */
    public static String toUpperCaseOnly(String s) {
        return s != null? s.toUpperCase().replaceAll("[^0-9A-Z]", ""): null;
    }

    /**
     * 使用StringTokenizer分割字符串，避免正则
     * @param source 待分割的字符串
     * @param delim 分割符
     * @return 按照分割符分割好的字符串数组
     */
    public static String[] quickSplit(String source, String delim) {
        if (null == source || null == delim) {
            throw new NullPointerException();
        }
        StringTokenizer tokenizer = new StringTokenizer(source, delim);
        String[] result = new String[tokenizer.countTokens()];
        for (int i = 0; i < result.length; i++) {
            result[i] = tokenizer.nextToken();
        }
        return result;
    }

    /**
     * 去掉指定字符串的开头的指定字符
     * @param str 原始字符串
     * @param trim 要删除的字符串
     * @return lk_user_name -> user_name
     */
    public static String trimStartStr(String str, String trim) {
        // null或者空字符串的时候不处理
        if (str == null || str.length() == 0 || trim == null || trim.length() == 0) {
            return str;
        }
        // 要删除的字符串结束位置
        int end;
        Pattern pattern = Pattern.compile("[" + trim + "]*+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        if (matcher.lookingAt()) {
            end = matcher.end();
            str = str.substring(end);
        }
        return str;
    }
}
