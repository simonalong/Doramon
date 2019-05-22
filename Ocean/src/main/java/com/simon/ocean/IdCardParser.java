package com.simon.ocean;

import com.alibaba.fastjson.JSON;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 身份证号解析器
 * @author zhouzhenyong
 * @since 2019/3/9 下午5:31
 */
public class IdCardParser {

    /**
     * 身份证正则表达式
     */
    private static final String ID_CARD_REGEX = "^(((([1-6]\\d)\\d{2})\\d{2})(((19|([2-9]\\d))\\d{2})((0[1-9])|(10|11|12))([0-2][1-9]|10|20|3[0-1]))((\\d{2})(\\d)[0-9Xx]))$";
    private static final Pattern PATTERN = Pattern.compile(ID_CARD_REGEX);
    /**
     * 全国省市数据映射，省市数据是这么多，因此开辟这么多数据
     */
    private static Map<String, Node> addressMap = new HashMap<>(3462);
    /**
     * 星座日期Map
     */
    private static Map<String, Pair<Integer, Integer>> constellationMap= new HashMap<>(12);
    private static IdCardParser instance = new IdCardParser();

    private IdCardParser() {
        constellationMap.put("水瓶座", new Pair<>(120, 218));
        constellationMap.put("双鱼座", new Pair<>(219, 320));
        constellationMap.put("白羊座", new Pair<>(321, 419));
        constellationMap.put("金牛座", new Pair<>(420, 520));
        constellationMap.put("双子座", new Pair<>(521, 621));
        constellationMap.put("巨蟹座", new Pair<>(622, 722));
        constellationMap.put("狮子座", new Pair<>(723, 822));
        constellationMap.put("处女座", new Pair<>(823, 922));
        constellationMap.put("天秤座", new Pair<>(923, 1023));
        constellationMap.put("天蝎座", new Pair<>(1024, 1122));
        constellationMap.put("射手座", new Pair<>(1123, 1221));
        constellationMap.put("摩羯座", new Pair<>(1222, 119));
    }

    private String idCardStr;

    public static IdCardParser getInstance() {
        return instance;
    }

    public static IdCardParser getInstance(String idCardStr) {
        instance.setIdCard(idCardStr);
        return instance;
    }

    /**
     * 初始化省市区码和name文档
     * @param contentJson json格式, 数组的json，每个对象都是code, name, children
     */
    public static void initFromContent(String contentJson) {
        init(JSON.parseArray(contentJson, Map.class));
    }

    @SuppressWarnings("unchecked")
    private static void init(List<Map> dataMap) {
        if (!CollectionUtils.isEmpty(dataMap)) {
            dataMap.forEach(map -> {
                String code = (String) map.get("code");
                String name = (String) map.get("name");
                addressMap.put(code, new Node(code, name));

                // 迭代录入
                init((List<Map>) map.get("children"));
            });
        }
    }

    /**
     * 校验身份证是否有效
     */
    public boolean valid() {
        return valid(idCardStr);
    }

    /**
     * 通过身份证号码解析地址信息：340103xxxxxx -> 安徽省合肥市庐阳区
     */
    public String parseAddress() {
        return parseAddress(idCardStr);
    }

    /**
     * 通过身份证号码解析地址信息：
     * xxxxxx19890312xxxxxx -> 19890312对应的Date
     */
    public Date parseBirthday() {
        return parseBirthday(idCardStr);
    }

    /**
     * 通过身份证号码解析地址信息：
     * xxxxxxxxxx1x -> 男
     * xxxxxxxxxx4x -> 女
     */
    public String parseGender() {
        return parseGender(idCardStr);
    }

    /**
     * 通过身份证号码解析星座信息
     * xxxxxx19890312xxxxxx -> 双鱼座
     */
    public String parseConstellation() {
        return parseConstellation(idCardStr);
    }

    /**
     * 通过身份证号码解析年龄信息，我们这里认为过了这个身份证中的生日就才算一岁
     * xxxxxx19890312xxxxxx -> 29
     */
    public Integer parseAge() {
        return parseAge(idCardStr);
    }

    public void setIdCard(String idCardStr) {
        this.idCardStr = idCardStr;
    }

    /**
     * 校验身份证是否有效
     */
    public static boolean valid(String idCardStr) {
        return PATTERN.matcher(idCardStr).matches();
    }

    /**
     * 通过身份证号码解析地址信息：340103xxxxxx -> 安徽省合肥市庐阳区
     * 地址解析需要先调用函数{@code IdCardParser#initFromContent}将省市对应的码和名字注册进去
     */
    public static String parseAddress(String idCardStr) {
        String province, city, country;

        Matcher matcher = PATTERN.matcher(idCardStr);
        StringBuilder address = new StringBuilder();
        if (matcher.find()) {
            // 省份
            province = matcher.group(4);
            // 市区
            city = matcher.group(3);
            // 县区
            country = matcher.group(2);

            if (!CollectionUtils.isEmpty(addressMap)) {
                if (addressMap.containsKey(province)) {
                    address.append(addressMap.get(province).getName());
                }
                if (addressMap.containsKey(city)) {
                    address.append(addressMap.get(city).getName());
                }
                if (addressMap.containsKey(country)) {
                    address.append(addressMap.get(country).getName());
                }
            }
        }
        return address.toString();
    }

    /**
     * 通过身份证号码解析地址信息：
     * xxxxxx19890312
     * xxxxxx -> 19890312对应的Date
     */
    public static Date parseBirthday(String idCardStr) {
        Matcher matcher = PATTERN.matcher(idCardStr);
        if (matcher.find()) {
            // 分组获取出生年月日
            String birthdayStr = matcher.group(5);
            if (!StringUtils.isEmpty(birthdayStr)) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                try {
                    return dateFormat.parse(birthdayStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return new Date(0);
    }

    /**
     * 通过身份证号码解析地址信息：
     * xxxxxxxxxx1x -> 男
     * xxxxxxxxxx4x -> 女
     */
    public static String parseGender(String idCardStr) {
        Matcher matcher = PATTERN.matcher(idCardStr);
        if (matcher.find()) {
            // 捕获组获取男女
            String genderStr = matcher.group(15);
            if (!StringUtils.isEmpty(genderStr)) {
                Integer gender = Integer.valueOf(genderStr);
                if (1 == gender % 2) {
                    return "男";
                } else {
                    return "男";
                }
            }
        }
        return "未知";
    }

    /**
     * 通过身份证号码解析星座信息
     * xxxxxx19890312xxxxxx -> 双鱼座
     */
    public static String parseConstellation(String idCardStr) {
        Matcher matcher = PATTERN.matcher(idCardStr);
        if (matcher.find()) {
            // 获取月
            String monthStr = matcher.group(9);
            // 获取日
            String dayStr = matcher.group(12);
            if (!StringUtils.isEmpty(monthStr) && !StringUtils.isEmpty(dayStr)) {
                Integer month = Integer.valueOf(monthStr);
                Integer day = Integer.valueOf(dayStr);
                Integer dayNum = month * 100 + day;

                if (!CollectionUtils.isEmpty(constellationMap)){
                    // 先排除特殊情况
                    if (dayNum <= 119 || dayNum >= 1222){
                        return "摩羯座";
                    }

                    return constellationMap.entrySet().stream()
                        .filter(e -> (dayNum >= e.getValue().getKey() && dayNum <= e.getValue().getValue())).findFirst()
                        .get().getKey();
                }
            }
        }
        return "未知";
    }

    /**
     * 通过身份证号码解析年龄信息，我们这里认为过了这个身份证中的生日就才算一岁
     * xxxxxx19890312xxxxxx -> 29
     */
    public static Integer parseAge(String idCardStr) {
        Matcher matcher = PATTERN.matcher(idCardStr);
        if (matcher.find()) {
            // 获取年
            String yearStr = matcher.group(6);
            // 获取月
            String monthStr = matcher.group(9);
            // 获取日
            String dayStr = matcher.group(12);
            if (!StringUtils.isEmpty(monthStr) && !StringUtils.isEmpty(dayStr)) {
                Integer month = Integer.valueOf(monthStr);
                Integer day = Integer.valueOf(dayStr);
                Integer birthDay = month * 100 + day;
                Date currentTime = new Date();

                Integer currentMonthDay = getCurrentMonthDay(currentTime);
                Integer currentYear = getCurrentYear(currentTime);

                Integer birthYear = Integer.valueOf(yearStr);
                if (currentMonthDay > birthDay){
                    if(currentYear >= birthYear){
                        return currentYear - birthYear;
                    }
                } else {
                    if(currentYear >= birthYear){
                        return currentYear - birthYear - 1;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * 获取当前时间的月和日，比如：3月12，则返回312
     */
    private static Integer getCurrentMonthDay(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String dataStr = format.format(date);
        String monthStr = dataStr.substring(4, 6);
        String dayStr = dataStr.substring(6);

        if (!StringUtils.isEmpty(monthStr) && !StringUtils.isEmpty(dayStr)) {
            Integer month = Integer.valueOf(monthStr);
            Integer day = Integer.valueOf(dayStr);
            return month * 100 + day;
        }
        return 0;
    }

    /**
     * 获取当前时间的年，比如：2018年3月 -> 2018
     */
    private static Integer getCurrentYear(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        String yearStr = format.format(date);
        if(!StringUtils.isEmpty(yearStr)){
            return Integer.valueOf(yearStr);
        }
        return 0;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    private static class Node {
        private String code;
        private String name;
    }
}
