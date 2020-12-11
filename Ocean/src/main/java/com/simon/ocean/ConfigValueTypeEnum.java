package com.simon.ocean;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shizi
 * @since 2020/9/14 9:58 上午
 */
@AllArgsConstructor
public enum ConfigValueTypeEnum {

    /**
     * yml配置
     */
    YAML("yml配置"),
    /**
     * properties配置
     */
    PROPERTIES("properties配置"),
    /**
     * 打包中
     */
    JSON("json配置"),
    /**
     * string字符配置
     */
    STRING("string字符配置");

    @Getter
    private final String desc;

    private static final Map<Integer, ConfigValueTypeEnum> indexEnumMap;
    private static final Map<String, ConfigValueTypeEnum> nameEnumMap;

    static {
        indexEnumMap = Arrays.stream(ConfigValueTypeEnum.values()).collect(Collectors.toMap(ConfigValueTypeEnum::ordinal, e -> e));
        nameEnumMap = Arrays.stream(ConfigValueTypeEnum.values()).collect(Collectors.toMap(ConfigValueTypeEnum::name, e -> e));
    }

    public static ConfigValueTypeEnum parse(Integer index) {
        if (!indexEnumMap.containsKey(index)) {
            throw new RuntimeException("不支持下标: " + index);
        }
        return indexEnumMap.get(index);
    }

    public static ConfigValueTypeEnum parse(String name) {
        if (!nameEnumMap.containsKey(name)) {
            throw new RuntimeException("不支持name: " + name);
        }
        return nameEnumMap.get(name);
    }
}
