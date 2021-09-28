package com.simon.ocean;

import com.alibaba.fastjson.JSON;
import com.amihaiemil.eoyaml.*;
import com.simon.ocean.exception.ValueChangeException;
import com.simon.ocean.exception.ValueCheckException;
import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * yaml与其他各种格式之间互转
 * <p>
 * <ul>
 *     <li>1.yaml <---> properties</li>
 *     <li>2.yaml <---> json</li>
 *     <li>3.yaml <---> map</li>
 *     <li>4.yaml <---> list</li>
 *     <li>5.yaml <---> kvList</li>
 * </ul>
 *
 * @author shizi
 * @since 2020/9/14 3:17 下午
 */
@Slf4j
@SuppressWarnings("all")
@UtilityClass
public class YamlUtil {

    /**
     * 换行符
     */
    private final String NEW_LINE = "\n";
    /**
     * 注释标识
     */
    private final String REMARK_PRE = "# ";
    /**
     * 缩进空格
     */
    private final String INDENT_BLANKS = "  ";
    /**
     * 分号连接符
     */
    private final String SIGN_SEMICOLON = ":";
    /**
     * 等号连接符
     */
    private final String SIGN_EQUAL = "=";
    /**
     * 点
     */
    private final String DOT = ".";
    /**
     * 数组缩进
     */
    private final String ARRAY_BLANKS = "- ";
    /**
     * yaml的value换行符
     */
    private final String yaml_NEW_LINE_DOM = "|\n";
    private final Pattern rangePattern = Pattern.compile("^(.*)\\[(\\d*)\\]$");
    /**
     * 格式转换的缓存
     */
    private final Map<String, Object> typeContentMap = new ConcurrentHashMap<>();

    /**
     * 判断类型是否是yaml类型
     *
     * @param content     yaml 内容
     * @return true：是yaml，false：不是
     */
    public boolean isYaml(String content) {
        if (isEmpty(content)) {
            return false;
        }
        return cacheCompute("isYaml", content, () -> {
            if (!content.contains(":") && !content.contains("-")) {
                return false;
            }

            try {
                checkYaml(content);
                return true;
            } catch (ValueCheckException e) {
                return false;
            }
        });
    }

    /**
     * 判断是否是yaml类型
     *
     * @param content 内容
     * @throws ValueCheckException 核查异常
     */
    public void checkYaml(String content) {
        if (isEmpty(content)) {
            throw new ValueCheckException("yaml内容为空");
        }
        if (!content.contains(":") && !content.contains("-")) {
            throw new ValueCheckException("yaml内容不包含\":\"也不包含\"-\"");
        }
        if (content.contains("---\n")) {
            throw new ValueCheckException("yaml内容不支持 --- 的导入");
        }

        try {
            yamlToProperties(content);
        } catch (ValueChangeException e) {
            throw new ValueCheckException("内容不是严格yaml类型;" + e.getMessage());
        }
    }

    /**
     * 判断是否是properties类型
     *
     * @param content     内容
     * @return true：是properties类型，false：不是properties类型
     */
    public boolean isProperties(String content) {
        if (isEmpty(content)) {
            return false;
        }
        return cacheCompute("isProperties", content, () -> {
            if (!content.contains("=")) {
                return false;
            }

            try {
                checkProperties(content);
                return true;
            } catch (ValueCheckException e) {
                return false;
            }
        });
    }

    /**
     * 判断是否是properties类型
     *
     * @param content 内容
     * @throws ValueCheckException 核查异常
     */
    public void checkProperties(String content) {
        if (isEmpty(content)) {
            throw new ValueCheckException("properties内容为空");
        }
        if (!content.contains("=")) {
            throw new ValueCheckException("properties内容不包含\"=\"");
        }
        try {
            checkYaml(propertiesToYaml(content));
        } catch (Throwable e) {
            throw new ValueCheckException("内容不是严格properties类型;" + e.getMessage());
        }
    }

    /**
     * 判断是否是json类型
     *
     * @param content 内容
     * @return true：是json类型，false：不是json类型
     */
    public boolean isJson(String content) {
        if (isEmpty(content)) {
            return false;
        }
        return cacheCompute("isJson", content, () -> {
            if (!content.startsWith("{") && !content.startsWith("[")) {
                return false;
            }

            try {
                checkJson(content);
                return true;
            } catch (ValueCheckException e) {
                return false;
            }
        });
    }

    /**
     * 判断是否是json对象类型
     *
     * @param content     内容
     * @return true：是json对象类型，false：不是json对象类型
     */
    public boolean isJsonObject(String content) {
        if (isEmpty(content)) {
            return false;
        }
        return cacheCompute("isJsonObject", content, () -> {
            try {
                checkJsonObject(content);
                return true;
            } catch (ValueCheckException e) {
                return false;
            }
        });
    }

    /**
     * 判断是否是json数组类型
     *
     * @param content     内容
     * @return true：是json数组类型，false：不是json数组类型
     */
    public boolean isJsonArray(String content) {
        if (isEmpty(content)) {
            return false;
        }
        return cacheCompute("isJsonArray", content, () -> {
            try {
                checkJsonArray(content);
                return true;
            } catch (ValueCheckException e) {
                return false;
            }
        });
    }

    /**
     * 判断是否是json类型
     *
     * @param content 内容
     * @throws ValueCheckException 核查异常
     */
    public void checkJson(String content) {
        if (isEmpty(content)) {
            throw new ValueCheckException("json内容不是严格json类型，因为内容为空");
        }

        // 先核查是否是object
        if (content.startsWith("{")) {
            try {
                JSON.parseObject(content);
            } catch (Throwable e) {
                throw new ValueCheckException("json内容不是严格json对象类型;" + e.getMessage());
            }
        } else if (content.startsWith("[")) {
            try {
                JSON.parseArray(content);
            } catch (Throwable e) {
                throw new ValueCheckException("json内容不是严格json数组类型;" + e.getMessage());
            }
        } else {
            throw new ValueCheckException("json内容不是json类型，因为没有\"{\"也没有\"[\"开头");
        }
    }

    /**
     * 判断是否是json对象类型
     *
     * @param content 内容
     * @throws ValueCheckException 核查异常
     */
    public void checkJsonObject(String content) {
        if (isEmpty(content)) {
            throw new ValueCheckException("json内容不是严格json对象类型，因为内容为空");
        }
        try {
            JSON.parseObject(content);
        } catch (Throwable e) {
            throw new ValueCheckException("内容不是严格json对象类型;" + e.getMessage());
        }
    }

    /**
     * 判断是否是json数组类型
     *
     * @param content 内容
     * @throws ValueCheckException 核查异常
     */
    public void checkJsonArray(String content) {
        if (isEmpty(content)) {
            throw new ValueCheckException("json内容不是严格json数组类型，因为内容为空");
        }
        try {
            JSON.parseArray(content);
        } catch (Throwable e) {
            throw new ValueCheckException("内容不是严格json对象类型;" + e.getMessage());
        }
    }

    /**
     * yaml格式转properties
     *
     * @param key key
     * @param content 对应的yaml内容
     * @return properties内容
     * @throws ValueChangeException 转换异常
     */
    public String yamlToProperties(String key, String content) {
        if (isEmpty(content)) {
            return null;
        }
        return cacheCompute("yamlToProperties", key, content, () -> {
            try {
                if (!content.contains(":") && !content.contains("-")) {
                    return null;
                }

                if (content.trim().startsWith("-")) {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put(key, yamlToList(content));
                    return yamlToProperties(mapToYaml(dataMap));
                }

                return propertiesAppendPrefixKey(key, yamlToProperties(content));
            } catch (Throwable e) {
                throw new ValueChangeException("yaml 转换到 properties异常：" + e.getMessage());
            }
        });
    }

    /**
     * yaml格式转换到properties
     *
     * @param content yaml内容
     * @return properties内容
     * @throws ValueChangeException 转换异常
     */
    public String yamlToProperties(String content) {
        if (isEmpty(content)) {
            return null;
        }
        return cacheCompute("yamlToProperties", content, () -> {
            try {
                if (!content.contains(":") && !content.contains("-")) {
                    return null;
                }

                if (content.trim().startsWith("-")) {
                    throw new ValueChangeException("不支持数组的yaml转properties");
                }

                List<String> propertiesList = new ArrayList<>();
                Map<String, String> remarkMap = new LinkedHashMap<>();
                Map<String, Object> valueMap = yamlToMap(content);

                // 读取yaml的注释
                yamlToRemarkMap(remarkMap, Yaml.createYamlInput(content).readYamlMapping(), "");
                formatYamlToProperties(propertiesList, remarkMap, valueMap, "");
                return propertiesList.stream().filter(e -> null != e && !"".equals(e)).reduce((a, b) -> a + NEW_LINE + b).orElse("");
            } catch (Throwable e) {
                throw new ValueChangeException(e);
            }
        });
    }

    public Properties yamlToPropertiesValue(String content) {
        if (isEmpty(content)) {
            return null;
        }
        return cacheCompute("yamlToProperties", content, () -> {
            try {
                if (!content.contains(":") && !content.contains("-")) {
                    return null;
                }

                if (content.trim().startsWith("-")) {
                    return null;
                }

                Properties properties = new Properties();
                Map<String, String> remarkMap = new LinkedHashMap<>();
                Map<String, Object> valueMap = yamlToMap(content);

                // 读取yaml的注释
                yamlToRemarkMap(remarkMap, Yaml.createYamlInput(content).readYamlMapping(), "");
                formatYamlToPropertiesValue(properties, remarkMap, valueMap, "");
                return properties;
            } catch (Throwable e) {
                throw new ValueChangeException(e);
            }
        });
    }

    /**
     * 将两个Yaml进行完全的合并
     *
     * @param aYaml
     * @param bYaml
     * @return 生成新的yaml
     */
    public String mergeFromYaml(String aYaml, String bYaml) {
        Properties aProperties = yamlToPropertiesValue(aYaml);
        Properties bProperties = yamlToPropertiesValue(bYaml);

        aProperties.putAll(bProperties);

        return propertiesToYaml(aProperties);
    }

    /**
     * 将两个map进行完全的合并
     *
     * @param aMap
     * @param bMap
     * @return 生成新的map
     */
    public Map<String, Object> mergeFromMap(Map<String, Object> aMap, Map<String, Object> bMap) {
        if (null == aMap || null == bMap) {
            if (null == aMap) {
                return bMap;
            }
            return aMap;
        }

        return yamlToMap(mergeFromYaml(mapToYaml(aMap), mapToYaml(bMap)));
    }

    /**
     * properties 转换到 yaml
     *
     * @param properties properties内容
     * @return yaml内容
     * @throws ValueChangeException 转换异常
     */
    public String propertiesToYaml(Properties properties) {
        if (properties.isEmpty()) {
            return null;
        }
        return cacheCompute("propertiesToYaml", properties, () -> {
            StringBuilder stringBuilder = new StringBuilder();
            properties.forEach((k, v) -> stringBuilder.append(k).append("=").append(v).append(NEW_LINE));
            return propertiesToYaml(stringBuilder.toString());
        });
    }

    /**
     * properties类型转换到json
     *
     * @param properties properties 内容
     * @return json内容
     * @throws ValueChangeException 转换异常
     */
    public String propertiesToJson(Properties properties) {
        return yamlToJson(propertiesToYaml(properties));
    }

    /**
     * properties内容转换到json
     *
     * @param content properties内容
     * @return json内容
     * @throws ValueChangeException 转换异常
     */
    public String propertiesToJson(String content) {
        return yamlToJson(propertiesToYaml(content));
    }

    /**
     * properties内容转换到map
     *
     * @param content properties内容
     * @return map内容
     * @throws ValueChangeException 转换异常
     */
    public Map<String, Object> propertiesToMap(String content) {
        if (isEmpty(content)) {
            return null;
        }
        return cacheCompute("propertiesToMap", content, () -> {
            if (!content.contains("=")) {
                return null;
            }

            Map<String, Object> resultMap = new HashMap<>();
            List<String> propertiesLineWordList = getPropertiesItemLineList(content);

            for (String line : propertiesLineWordList) {
                String lineTem = line.trim();
                if (!"".equals(lineTem)) {
                    int index = lineTem.indexOf("=");
                    if (index > -1) {
                        String key = lineTem.substring(0, index);
                        String value = lineTem.substring(index + 1);

                        // 对于yaml中换行的添加|用于保留换行
                        if (value.contains("\n")) {
                            value = yaml_NEW_LINE_DOM + value;
                        }
                        resultMap.put(key, value);
                    }
                }
            }
            return resultMap;
        });
    }

    /**
     * properties 转换到 yaml
     *
     * @param content properties内容
     * @return yaml内容
     * @throws ValueChangeException 转换异常
     */
    public String propertiesToYaml(String content) {
        if (isEmpty(content)) {
            return null;
        }
        return cacheCompute("propertiesToYaml", content, () -> {
            if (!content.contains("=")) {
                return null;
            }
            try {
                List<String> yamlLineList = new ArrayList<>();
                List<String> propertiesLineWordList = getPropertiesItemLineList(content);

                List<YamlNode> YamlNodes = new ArrayList<>();
                StringBuilder projectRemark = new StringBuilder();
                StringBuilder remark = new StringBuilder();
                for (String line : propertiesLineWordList) {
                    String lineTem = line.trim();
                    if (!"".equals(lineTem)) {
                        if (lineTem.startsWith("#")) {
                            if (0 != remark.length()) {
                                projectRemark.append(remark.toString());
                                remark.delete(0, remark.length());
                            }
                            remark.append(lineTem);
                            continue;
                        }
                        int index = lineTem.indexOf("=");
                        if (index > -1) {
                            String key = lineTem.substring(0, index);
                            String value = lineTem.substring(index + 1);
                            // 对于yaml中换行的添加|用于保留换行
                            if (value.contains("\n")) {
                                value = yaml_NEW_LINE_DOM + value;
                            }

                            final List<String> lineWordList = new ArrayList<>(Arrays.asList(key.split("\\.")));
                            wordToNode(lineWordList, YamlNodes, null, false, null, appendSpaceForArrayValue(value), projectRemark.toString(), remark.toString());
                        }
                        // 删除本地保留
                        remark.delete(0, remark.length());
                        projectRemark.delete(0, projectRemark.length());
                    }
                }
                formatPropertiesToYaml(yamlLineList, YamlNodes, false, "");
                return yamlLineList.stream().reduce((a, b) -> a + "\n" + b).orElse("") + "\n";
            } catch (Throwable e) {
                throw new ValueChangeException("properties 转换到 yaml异常：" + e.getMessage());
            }
        });
    }

    /**
     * yaml 转换到 对象
     *
     * @param content properties内容
     * @return yaml内容
     * @throws ValueChangeException 转换异常
     */
    public Object yamlToObject(String content) {
        if (isEmpty(content)) {
            return null;
        }
        return cacheCompute("yamlToObject", content, () -> {
            if (!content.contains(":") && !content.contains("-")) {
                return null;
            }

            if (content.trim().startsWith("-")) {
                return yamlToList(content);
            }

            return yamlToMap(content);
        });
    }

    /**
     * yaml 转 map
     *
     * 由于eo-yaml对map转换支持会默认将一些key添加字符，这里就用snakeyaml工具做
     * @return map 对象
     * @throws ValueChangeException 转换异常
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Map<String, Object> yamlToMap(String content) {
        if (isEmpty(content)) {
            return null;
        }
        return cacheCompute("yamlToMap", content, () -> {
            if (!content.contains(":") && !content.contains("-")) {
                return null;
            }

            try {
                Map<String, Object> resultMap = new HashMap<>();
                org.yaml.snakeyaml.Yaml yml = new org.yaml.snakeyaml.Yaml();
                Map result = yml.loadAs(content, Map.class);
                Set<Map.Entry<?, ?>> entrySet = result.entrySet();
                for (Map.Entry<?, ?> entry : entrySet) {
                    resultMap.put(String.valueOf(entry.getKey()), entry.getValue());
                }
                return resultMap;
            } catch (Throwable e) {
                throw new ValueChangeException("yml 转换到 map 异常：" + e.getMessage());
            }
        });
    }

    /**
     * yaml 转 map
     *
     * @param content yaml内容
     * @return 集合内容
     * @throws ValueChangeException 转换异常
     */
    public List<Object> yamlToList(String content) {
        if (isEmpty(content)) {
            return null;
        }
        return cacheCompute("yamlToList", content, () -> {
            if (!content.trim().startsWith("-")) {
                return null;
            }

            try {
                org.yaml.snakeyaml.Yaml yml = new org.yaml.snakeyaml.Yaml();
                return yml.load(content);
            } catch (Throwable e) {
                throw new ValueChangeException("yml 转换到 map 异常：" + e.getMessage());
            }
        });
    }

    /**
     * map 转 yaml
     *
     * @param contentMap map内容
     * @return yaml内容
     * @throws ValueChangeException 转换异常
     */
    public String mapToYaml(Map<String, Object> contentMap) {
        if (isEmpty(contentMap)) {
            return null;
        }
        return cacheCompute("yamlToList", contentMap, () -> {
            try {
                org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
                String originalYaml = yaml.dumpAsMap(contentMap);
                // 由于snakeyaml对数组缩进支持不够好，这里做一层缩进
                return yamlFormatForMap(originalYaml);
            } catch (Throwable e) {
                throw new ValueChangeException("map 转换到 yml 异常：" + e.getMessage());
            }
        });
    }

    /**
     * 集合内容转yaml
     *
     * @param contentList 集合内容
     * @return yaml内容
     * @throws ValueChangeException 转换异常
     */
    public String listToYaml(List<Object> contentList) {
        if (isEmpty(contentList)) {
            return null;
        }
        return cacheCompute("listToYaml", contentList, () -> {
            try {
                org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
                return yaml.dumpAs(contentList, null, DumperOptions.FlowStyle.BLOCK);
            } catch (Throwable e) {
                throw new ValueChangeException("map 转换到 yml 异常：" + e.getMessage());
            }
        });
    }

    /**
     * yaml 转 json
     *
     * @param content yaml内容
     * @return json内容
     * @throws ValueChangeException 转换异常
     */
    public String yamlToJson(String content) {
        if (isEmpty(content)) {
            return null;
        }
        return cacheCompute("yamlToJson", content, () -> {
            if (!content.contains(":") && !content.contains("-")) {
                return null;
            }

            try {
                return JSON.toJSONString(yamlToObject(content));
            } catch (Throwable e) {
                throw new ValueChangeException("yaml 转换到 json 异常：" + e.getMessage());
            }
        });
    }

    /**
     * json 转 对象
     *
     * @param content json内容
     * @return object内容
     * @throws ValueChangeException 转换异常
     */
    public Object jsonToObject(String content) {
        if (isEmpty(content)) {
            return null;
        }
        return cacheCompute("jsonToObject", content, () -> {
            if (!content.startsWith("{") && !content.startsWith("[")) {
                return null;
            }

            try {
                if (isJsonObject(content)) {
                    return JSON.parseObject(content);
                } else if (isJsonArray(content)) {
                    return JSON.parseArray(content);
                }
                throw new ValueChangeException("content 不是json类型");
            } catch (Throwable e) {
                throw new ValueChangeException("json 转换到 yaml 异常：" + e.getMessage());
            }
        });
    }

    /**
     * json 转 yaml
     *
     * @param content json内容
     * @return yaml内容
     * @throws ValueChangeException 转换异常
     */
    public String jsonToYaml(String content) {
        if (isEmpty(content)) {
            return null;
        }
        return cacheCompute("jsonToYaml", content, () -> {
            if (!content.startsWith("{") && !content.startsWith("[")) {
                return null;
            }

            try {
                if (isJsonObject(content)) {
                    return mapToYaml(JSON.parseObject(content));
                } else if (isJsonArray(content)) {
                    return listToYaml(JSON.parseArray(content));
                }
                throw new ValueChangeException("content 不是json类型");
            } catch (Throwable e) {
                throw new ValueChangeException("json 转换到 yaml 异常：" + e.getMessage());
            }
        });
    }

    /**
     * yaml类型转换为 k-v的集合
     *
     * @param content yaml内容
     * @return kv集合
     * @throws ValueChangeException 转换异常
     */
    public List<Map.Entry<String, String>> yamlToKVList(String content) {
        if (isEmpty(content)) {
            return null;
        }
        return cacheCompute("yamlToKVList", content, () -> {
            if (!content.contains(":") && !content.contains("-")) {
                return null;
            }

            try {
                String propertiesContent = yamlToProperties(content);
                return getPropertiesItemLineList(propertiesContent).stream().map(e -> {
                    int index = e.indexOf("=");
                    String key = e.substring(0, index);
                    String value = e.substring(index + 1);
                    return new AbstractMap.SimpleEntry<>(key, value);
                }).collect(Collectors.toList());
            } catch (Throwable e) {
                throw new ValueChangeException("yaml 转换到 kv-list 异常：" + e.getMessage());
            }
        });
    }

    /**
     * k-v的集合类型转yaml
     *
     * @param kvStringList kv集合
     * @return yaml内容
     * @throws ValueChangeException 转换异常
     */
    public String kvListToYaml(List<Map.Entry<String, Object>> kvStringList) {
        if (isEmpty(kvStringList)) {
            return null;
        }
        return cacheCompute("kvListToYaml", kvStringList, () -> {
            try {
                String propertiesContent = kvStringList.stream().map(e -> e.getKey() + "=" + e.getValue()).reduce((a, b) -> a + "\n" + b).orElse("");
                return propertiesToYaml(propertiesContent);
            } catch (Throwable e) {
                throw new ValueChangeException("kv-list 转换到 yaml 异常：" + e.getMessage());
            }
        });
    }

    public String kvToYaml(String key, String value, ConfigValueTypeEnum valueTypeEnum) {
        if (isEmpty(key)) {
            return null;
        }
        return cacheCompute("kvToYaml", key, value, () -> {
            try {
                return propertiesToYaml(kvToProperties(key, value, valueTypeEnum));
            } catch (Throwable e) {
                throw new ValueChangeException("kv 转换到 yaml 异常：" + e.getMessage());
            }
        });
    }

    public Map<String, Object> kvToMap(String key, String value, ConfigValueTypeEnum valueTypeEnum) {
        if (isEmpty(key)) {
            return null;
        }
        return cacheCompute("kvToMap", key, value, () -> propertiesToMap(kvToProperties(key, value, valueTypeEnum)));
    }

    public String kvToProperties(String key, String value, ConfigValueTypeEnum valueTypeEnum) {
        return cacheCompute("kvToProperties", key, value, () -> kvToProperties(key, value, null, valueTypeEnum));
    }

    /**
     * k-v的String类型转properties
     *
     * <p>其中key可能是a.b.c这种，而value可能是各种各样的类型，我们这里通过valueType进行区分
     *
     * @param key           主键
     * @param value         待转换的值
     * @param desc          注释
     * @param valueTypeEnum 值的类型，0：yaml，1：properties，2：json，3：string
     * @return 转换之后的yaml类型
     * @throws ValueChangeException 转换异常
     */
    public String kvToProperties(String key, String value, String desc, ConfigValueTypeEnum valueTypeEnum) {
        if (isEmpty(key)) {
            return null;
        }
        return cacheCompute("kvToProperties", key, value, () -> {
            try {
                // 将value对应的值先转换为properties类型，然后对key进行拼接，最后再统一转化为yaml格式
                StringBuilder propertiesResult = new StringBuilder();
                if (null != desc && !"".equals(desc)) {
                    propertiesResult.append("# ").append(desc).append("\n");
                }

                String propertiesContent;
                switch (valueTypeEnum) {
                    case YAML:
                        propertiesContent = yamlToProperties(key, value);
                        if (!isEmpty(propertiesContent)) {
                            propertiesResult.append(propertiesContent);
                        }
                        return propertiesResult.toString();
                    case JSON:
                        propertiesContent = yamlToProperties(key, jsonToYaml(value));
                        if (!isEmpty(propertiesContent)) {
                            propertiesResult.append(propertiesContent);
                        }
                        return propertiesResult.toString();
                    case PROPERTIES:
                        propertiesContent = propertiesAppendPrefixKey(key, value);
                        if (!isEmpty(propertiesContent)) {
                            propertiesResult.append(propertiesContent);
                        }
                        return propertiesResult.toString();
                    case STRING:
                        propertiesResult.append(key).append("=").append(appendSpaceForArrayValue(value));
                        return propertiesResult.toString();
                    default:
                        break;
                }

                return propertiesResult.toString();
            } catch (Throwable e) {
                throw new ValueChangeException("kv 转换到 properties 异常: " + e.getMessage());
            }
        });
    }

    public List<String> getPropertiesItemLineList(String content) {
        if (isEmpty(content)) {
            return Collections.emptyList();
        }
        return cacheCompute("getPropertiesItemLineList", content, () -> {
            if (!content.contains("=")) {
                return Collections.emptyList();
            }

            String[] lineList = content.split(NEW_LINE);
            List<String> itemLineList = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            for (String line : lineList) {
                // 处理多行数据
                if (line.endsWith("\\")) {
                    stringBuilder.append(line).append("\n");
                } else {
                    stringBuilder.append(line);
                    itemLineList.add(stringBuilder.toString());
                    stringBuilder.delete(0, stringBuilder.length());
                }
            }
            return itemLineList;
        });
    }

    private String propertiesAppendPrefixKey(String key, String propertiesContent) {
        return getPropertiesItemLineList(propertiesContent).stream().filter(e -> e.contains("=")).map(e -> {
            int index = e.indexOf("=");
            if (index > -1) {
                String keyTem = e.substring(0, index).trim();
                String valueTem = e.substring(index + 1).trim();
                return key + DOT + keyTem + "=" + valueTem;
            }
            return null;
        }).filter(Objects::nonNull).reduce((a, b) -> a + NEW_LINE + b).orElse(null);
    }

    /**
     * 针对有些yaml格式不严格，这里做不严格向严格的eo-yaml解析的转换
     * <p>
     * 对{@code
     * test:
     * - k1: 12
     * - k2: 22
     * }
     * 这种做一层缩进，由于snake的map转yaml后有缩进问题
     */
    private String yamlFormatForMap(String content) {
        if (isEmpty(content)) {
            return null;
        }
        return cacheCompute("yamlFormatForMap", content, () -> {
            if (!content.contains(":") && !content.contains("-")) {
                return null;
            }

            StringBuilder stringBuilder = new StringBuilder();
            String[] items = content.split("\n");
            Integer blankSize = null;
            // 判断是否在数组中
            boolean inArray = false;
            for (String item : items) {
                int innerBlankSize = item.substring(0, item.indexOf(item.trim())).length();
                // 数组
                if (item.trim().startsWith("- ")) {
                    if (inArray) {
                        // 多重数组，则多层嵌套
                        if (innerBlankSize > blankSize) {
                            stringBuilder.append(INDENT_BLANKS).append(INDENT_BLANKS).append(item).append("\n");
                            continue;
                        }
                    }
                    inArray = true;
                    blankSize = innerBlankSize;
                } else {
                    // 其他的字符
                    if (null != blankSize) {
                        if (innerBlankSize <= blankSize) {
                            inArray = false;
                        }
                    }
                }

                if (inArray) {
                    stringBuilder.append(INDENT_BLANKS).append(item).append("\n");
                } else {
                    stringBuilder.append(item).append("\n");
                }
            }
            return stringBuilder.toString();
        });
    }

    /**
     * 将key转换为yaml节点
     *
     * @param lineWordList      待转换的key，比如{@code k1.k2.k3=123}
     * @param nodeList          已经保存的节点数据
     * @param lastNodeArrayFlag 上一个节点是否数组类型
     * @param index             索引下标
     * @param value             解析的值
     * @param remark            当前value对应的注释
     */
    private void wordToNode(List<String> lineWordList, List<YamlNode> nodeList, YamlNode parentNode, Boolean lastNodeArrayFlag, Integer index, String value, String projectRemark,
        String remark) {
        if (lineWordList.isEmpty()) {
            if (lastNodeArrayFlag) {
                YamlNode node = new YamlNode();
                node.setValue(value);
                node.setRemark(remark);
                nodeList.add(node);
            }
        } else {
            String nodeName = lineWordList.get(0);

            Pair<String, Integer> nameAndIndex = peelArray(nodeName);
            nodeName = nameAndIndex.getKey();
            Integer nextIndex = nameAndIndex.getValue();

            YamlNode node = new YamlNode();
            node.setName(nodeName);
            node.setProjectRemark(projectRemark);
            node.setParent(parentNode);
            node.setRemark(remark);
            node.setLastNodeIndex(index);
            lineWordList.remove(0);

            //如果节点下面的子节点数量为0，则为终端节点，也就是赋值节点
            if (lineWordList.size() == 0) {
                if (null == nextIndex) {
                    node.setRemark(remark);
                    node.setValue(value);
                }
            }

            // nextIndex 不空，表示当前节点为数组，则之后的数据为他的节点数据
            if (null != nextIndex) {
                node.setArrayFlag(true);
                boolean hasEqualsName = false;
                //遍历查询节点是否存在
                for (YamlNode YamlNode : nodeList) {
                    //如果节点名称已存在，则递归添加剩下的数据节点
                    if (nodeName.equals(YamlNode.getName()) && YamlNode.getArrayFlag()) {
                        Integer yamlNodeIndex = YamlNode.getLastNodeIndex();
                        if (null == yamlNodeIndex || index.equals(yamlNodeIndex)) {
                            hasEqualsName = true;
                            wordToNode(lineWordList, YamlNode.getValueList(), node.getParent(), true, nextIndex, appendSpaceForArrayValue(value), null, remark);
                        }
                    }
                }
                //如果遍历结果为节点名称不存在，则递归添加剩下的数据节点，并把新节点添加到上级yamlTree的子节点中
                if (!hasEqualsName) {
                    wordToNode(lineWordList, node.getValueList(), node.getParent(), true, nextIndex, appendSpaceForArrayValue(value), null, remark);
                    nodeList.add(node);
                }
            } else {
                boolean hasEqualsName = false;
                //遍历查询节点是否存在
                for (YamlNode YamlNode : nodeList) {
                    if (!lastNodeArrayFlag) {
                        //如果节点名称已存在，则递归添加剩下的数据节点
                        if (nodeName.equals(YamlNode.getName())) {
                            hasEqualsName = true;
                            wordToNode(lineWordList, YamlNode.getChildren(), YamlNode, false, nextIndex, appendSpaceForArrayValue(value), null, remark);
                        }
                    } else {
                        //如果节点名称已存在，则递归添加剩下的数据节点
                        if (nodeName.equals(YamlNode.getName())) {
                            Integer yamlNodeIndex = YamlNode.getLastNodeIndex();
                            if (null == yamlNodeIndex || index.equals(yamlNodeIndex)) {
                                hasEqualsName = true;
                                wordToNode(lineWordList, YamlNode.getChildren(), YamlNode, true, nextIndex, appendSpaceForArrayValue(value), null, remark);
                            }
                        }
                    }
                }
                //如果遍历结果为节点名称不存在，则递归添加剩下的数据节点，并把新节点添加到上级yamlTree的子节点中
                if (!hasEqualsName) {
                    wordToNode(lineWordList, node.getChildren(), node, false, nextIndex, appendSpaceForArrayValue(value), null, remark);
                    nodeList.add(node);
                }
            }
        }
    }

    /**
     * 获取yaml中的注释
     *
     * @param remarkMap 解析后填充的注释map：key为a.b.c.d，value为对应的注释，去除掉前缀#后的数据
     * @param mapping   yaml解析后数据
     * @param prefix    前缀
     */
    private void yamlToRemarkMap(Map<String, String> remarkMap, YamlMapping mapping, String prefix) {
        if (null == mapping) {
            return;
        }
        for (com.amihaiemil.eoyaml.YamlNode node : mapping.keys()) {
            String nodeName = node.asScalar().value();
            String remark = mapping.value(node).comment().value();

            if (null != remark && !"".equals(remark)) {
                remarkMap.put(wrapKey(prefix, nodeName), remark);
            }

            yamlToRemarkMap(remarkMap, mapping.yamlMapping(node), wrapKey(prefix, nodeName));
        }
    }

    private String wrapKey(String prefix, String value) {
        if (isEmpty(prefix)) {
            return prefix + "." + value;
        }
        return value;
    }

    /**
     * 解析节点名字，为数组则返回数组名和节点下标
     * <p>
     * name.test[0] 将test和0进行返回
     *
     * @param nodeName 界面的名字
     * @return 如果是数组，则将数组名和解析后的下标返回
     */
    private Pair<String, Integer> peelArray(String nodeName) {
        String name = nodeName;
        Integer index = null;
        Matcher matcher = rangePattern.matcher(nodeName);
        if (matcher.find()) {
            String indexStr = matcher.group(2);
            if (null != indexStr) {
                index = Integer.valueOf(indexStr);
            }
            name = matcher.group(1);
        }

        return new Pair<>(name, index);
    }

    /**
     * 将yaml对应的这种value进行添加前缀空格，其中value为key1对应的value
     * {@code
     * test:
     * key1: |
     * value1
     * value2
     * value3
     * }
     * 对应的值
     * {@code
     * |
     * value1
     * value2
     * value3
     * }
     *
     * @param value 待转换的值比如{@code
     *              test:
     *              key1: |
     *              value1
     *              value2
     *              value3
     *              }
     * @return 添加前缀空格之后的处理
     * {@code
     * |
     * value1
     * value2
     * value3
     * }
     */
    private String appendSpaceForArrayValue(String value) {
        if (!value.startsWith(yaml_NEW_LINE_DOM)) {
            return value;
        }
        String valueTem = value.substring(yaml_NEW_LINE_DOM.length());
        return yaml_NEW_LINE_DOM + Arrays.stream(valueTem.split("\\n")).map(e -> {
            String tem = e;
            if (e.endsWith("\\")) {
                tem = e.substring(0, e.length() - 1);
            }
            return INDENT_BLANKS + tem;
        }).reduce((a, b) -> a + "\n" + b).orElse(valueTem);
    }

    private void formatPropertiesToYaml(List<String> yamlLineList, List<YamlNode> YamlNodes, Boolean lastNodeArrayFlag, String blanks) {
        Integer beforeNodeIndex = null;
        String equalSign;
        for (YamlNode YamlNode : YamlNodes) {
            String value = YamlNode.getValue();
            String remark = YamlNode.getRemark();

            equalSign = SIGN_SEMICOLON;
            if (null == value || "".equals(value)) {
                value = "";
            } else {
                equalSign = SIGN_SEMICOLON + " ";
            }
            YamlNode.resortValue();

            String name = YamlNode.getName();
            if (lastNodeArrayFlag) {
                if (null == name) {
                    yamlLineList.add(blanks + ARRAY_BLANKS + stringValueWrap(value));
                } else {
                    if (null != beforeNodeIndex && beforeNodeIndex.equals(YamlNode.getLastNodeIndex())) {
                        yamlLineList.add(blanks + INDENT_BLANKS + name + equalSign + stringValueWrap(value));
                    } else {
                        yamlLineList.add(blanks + ARRAY_BLANKS + name + equalSign + stringValueWrap(value));
                    }
                }
                beforeNodeIndex = YamlNode.getLastNodeIndex();
            } else {
                // 父节点为空，表示，当前为顶层
                if (null == YamlNode.getParent()) {
                    String remarkTem = getRemarkProject(YamlNode.getProjectRemark());
                    if (!"".equals(remarkTem)) {
                        yamlLineList.add(blanks + getRemarkProject(YamlNode.getProjectRemark()));
                    }
                }

                // 自己节点为数组，则添加对应的注释
                if (YamlNode.getArrayFlag()) {
                    if (null != remark && !"".equals(remark)) {
                        yamlLineList.add(blanks + remark);
                    }
                }
                yamlLineList.add(blanks + name + equalSign + stringValueWrap(value, remark));
            }

            if (YamlNode.getArrayFlag()) {
                if (lastNodeArrayFlag) {
                    formatPropertiesToYaml(yamlLineList, YamlNode.getValueList(), true, INDENT_BLANKS + INDENT_BLANKS + blanks);
                } else {
                    formatPropertiesToYaml(yamlLineList, YamlNode.getValueList(), true, INDENT_BLANKS + blanks);
                }
            } else {
                if (lastNodeArrayFlag) {
                    formatPropertiesToYaml(yamlLineList, YamlNode.getChildren(), false, INDENT_BLANKS + INDENT_BLANKS + blanks);
                } else {
                    formatPropertiesToYaml(yamlLineList, YamlNode.getChildren(), false, INDENT_BLANKS + blanks);
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void formatYamlToPropertiesValue(Properties properties, Map<String, String> remarkMap, Object object, String prefix) {
        if (null == object) {
            return;
        }
        if (object instanceof Map) {
            Map map = (Map) object;
            Set<?> set = map.keySet();
            for (Object key : set) {
                Object value = map.get(key);
                if (null == value) {
                    value = "";
                }
                if (value instanceof Map) {
                    formatYamlToPropertiesValue(properties, remarkMap, value, prefixWithDOT(prefix) + key);
                } else if (value instanceof Collection) {
                    Collection collection = (Collection) value;
                    if (!collection.isEmpty()) {
                        Iterator<?> iterator = collection.iterator();
                        int index = 0;
                        while (iterator.hasNext()) {
                            Object valueObject = iterator.next();
                            formatYamlToPropertiesValue(properties, remarkMap, valueObject, prefixWithDOT(prefix) + key + "[" + index + "]");
                            index = index + 1;
                        }
                    }
                } else if (value instanceof String) {
                    String valueStr = (String) value;
                    valueStr = valueStr.trim();
                    valueStr = valueStr.replace("\n", "\\\n");
                    properties.put(prefixWithDOT(prefix) + key, valueStr);
                } else {
                    properties.put(prefixWithDOT(prefix) + key, value);
                }
            }
        } else if (object instanceof Collection) {
            Collection collection = (Collection) object;
            if (!collection.isEmpty()) {
                Iterator<?> iterator = collection.iterator();
                int index = 0;
                while (iterator.hasNext()) {
                    Object valueObject = iterator.next();
                    formatYamlToPropertiesValue(properties, remarkMap, valueObject, prefix + "[" + index + "]");
                    index = index + 1;
                }
            }
        } else if (object.getClass().isArray()) {
            Object[] array = (Object[]) object;
            for (int index = 0; index < array.length; index++) {
                formatYamlToPropertiesValue(properties, remarkMap, array[index], prefix + "[" + index + "]");
            }
        } else if (object instanceof String) {
            String valueObject = (String) object;
            valueObject = valueObject.replace("\n", "\\\n");
            properties.put(prefix, valueObject);
        } else {
            properties.put(prefix, object);
        }
    }

    @SuppressWarnings("rawtypes")
    private void formatYamlToProperties(List<String> propertiesLineList, Map<String, String> remarkMap, Object object, String prefix) {
        if (null == object) {
            return;
        }
        if (object instanceof Map) {
            // 填充注释
            if (remarkMap.containsKey(prefix)) {
                propertiesLineList.add(REMARK_PRE + remarkMap.get(prefix));
            }

            Map map = (Map) object;
            Set<?> set = map.keySet();
            for (Object key : set) {
                Object value = map.get(key);
                if (null == value) {
                    value = "";
                }
                if (value instanceof Map) {
                    formatYamlToProperties(propertiesLineList, remarkMap, value, prefixWithDOT(prefix) + key);
                } else if (value instanceof Collection) {
                    Collection collection = (Collection) value;
                    if (!collection.isEmpty()) {
                        // 填充注释
                        if (remarkMap.containsKey(prefixWithDOT(prefix) + key)) {
                            propertiesLineList.add(REMARK_PRE + remarkMap.get(prefixWithDOT(prefix) + key));
                        }

                        Iterator<?> iterator = collection.iterator();
                        int index = 0;
                        while (iterator.hasNext()) {
                            Object valueObject = iterator.next();
                            formatYamlToProperties(propertiesLineList, remarkMap, valueObject, prefixWithDOT(prefix) + key + "[" + index + "]");
                            index = index + 1;
                        }
                    }
                } else if (value instanceof String) {
                    String valueStr = (String) value;
                    valueStr = valueStr.trim();
                    valueStr = valueStr.replace("\n", "\\\n");
                    // 填充注释
                    if (remarkMap.containsKey(prefixWithDOT(prefix) + key)) {
                        propertiesLineList.add(REMARK_PRE + remarkMap.get(prefixWithDOT(prefix) + key));
                    }

                    propertiesLineList.add(prefixWithDOT(prefix) + key + SIGN_EQUAL + valueStr);
                } else {
                    // 填充注释
                    if (remarkMap.containsKey(prefixWithDOT(prefix) + key)) {
                        propertiesLineList.add(REMARK_PRE + remarkMap.get(prefixWithDOT(prefix) + key));
                    }

                    propertiesLineList.add(prefixWithDOT(prefix) + key + SIGN_EQUAL + value);
                }
            }
        } else if (object instanceof Collection) {
            Collection collection = (Collection) object;
            if (!collection.isEmpty()) {
                // 填充注释
                if (remarkMap.containsKey(prefix)) {
                    propertiesLineList.add(REMARK_PRE + remarkMap.get(prefix));
                }

                Iterator<?> iterator = collection.iterator();
                int index = 0;
                while (iterator.hasNext()) {
                    Object valueObject = iterator.next();
                    formatYamlToProperties(propertiesLineList, remarkMap, valueObject, prefix + "[" + index + "]");
                    index = index + 1;
                }
            }
        } else if (object.getClass().isArray()) {
            Object[] array = (Object[]) object;
            for (int index = 0; index < array.length; index++) {
                formatYamlToProperties(propertiesLineList, remarkMap, array[index], prefix + "[" + index + "]");
            }
        } else if (object instanceof String) {
            String valueObject = (String) object;
            valueObject = valueObject.replace("\n", "\\\n");
            // 填充注释
            if (remarkMap.containsKey(prefix)) {
                propertiesLineList.add(REMARK_PRE + remarkMap.get(prefix));
            }

            propertiesLineList.add(prefix + SIGN_EQUAL + valueObject);
        } else {
            // 填充注释
            if (remarkMap.containsKey(prefix)) {
                propertiesLineList.add(REMARK_PRE + remarkMap.get(prefix));
            }

            propertiesLineList.add(prefix + SIGN_EQUAL + object);
        }
    }

    private String prefixWithDOT(String prefix) {
        if ("".equals(prefix)) {
            return prefix;
        }
        return prefix + DOT;
    }

    private String stringValueWrap(String value) {
        if (isEmpty(value)) {
            return "";
        }
        // 对数组的数据进行特殊处理
        if (value.startsWith("[") && value.endsWith("]")) {
            return "'" + value + "'";
        }
        return value;
    }

    private String stringValueWrap(String value, String remark) {
        if (isEmpty(value)) {
            return "";
        }
        // 对数组的数据进行特殊处理
        if (value.startsWith("[") && value.endsWith("]")) {
            return "'" + value + "'" + getRemark(remark);
        }

        return value + getRemark(remark);
    }

    private String getRemark(String remark) {
        if (null != remark && !"".endsWith(remark) && remark.startsWith("#")) {
            return " # " + remark.substring(1).trim();
        } else {
            return "";
        }
    }

    private String getRemarkProject(String remark) {
        if (null != remark && !"".endsWith(remark) && remark.startsWith("#")) {
            return " # " + remark.substring(1).trim();
        } else {
            return "";
        }
    }

    /**
     * 数据存在则返回，不存在则计算后添加到缓存中
     */
    @SuppressWarnings("unchecked")
    private <T> T cacheCompute(String funName, Object key, Supplier<T> biFunction) {
        String cacheKey = buildCacheKey(funName, key);
        if (typeContentMap.containsKey(cacheKey)) {
            return (T) typeContentMap.get(cacheKey);
        }
        T result = biFunction.get();
        if (null != result) {
            typeContentMap.put(cacheKey, result);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> T cacheCompute(String funName, Object key, Object value, Supplier<T> biFunction) {
        String cacheKey = buildCacheKey(funName, key, value);
        if (typeContentMap.containsKey(cacheKey)) {
            return (T) typeContentMap.get(cacheKey);
        }
        T result = biFunction.get();
        if (null != result) {
            typeContentMap.put(cacheKey, result);
        }
        return result;
    }

    private String buildCacheKey(String funName, Object... parameters) {
        StringBuilder stringBuilder = new StringBuilder(funName);
        for (Object parameter : parameters) {
            if (null != parameter) {
                stringBuilder.append(":").append(parameter.toString());
            }
        }
        return stringBuilder.toString();
    }

    private boolean isEmpty(String string) {
        return null == string || "".endsWith(string);
    }

    private boolean isEmpty(Collection<?> collection) {
        return null == collection || collection.isEmpty();
    }

    private boolean isEmpty(Map<?, ?> map) {
        return null == map || map.isEmpty();
    }

    @Data
    class YamlNode {

        private YamlNode parent;
        /**
         * 只有parent为null时候，该值才可能有值
         */
        private String projectRemark;
        /**
         * name
         */
        private String name;
        /**
         * value
         */
        private String value;
        /**
         * 注释
         */
        private String remark;

        /**
         * 子节点
         */
        private List<YamlNode> children = new ArrayList<>();

        /**
         * 数组标示：
         */
        private Boolean arrayFlag = false;
        /**
         * 存储的数组中的前一个节点的下标
         */
        private Integer lastNodeIndex;
        /**
         * 只有数组标示为true，下面的value才有值
         */
        private List<YamlNode> valueList = new ArrayList<>();

        /**
         * 将其中的value按照index下标顺序进行重拍
         */
        public void resortValue() {
            if (!arrayFlag || valueList.isEmpty()) {
                return;
            }

            // 升序
            valueList.sort((a, b) -> {
                if (null == a.getLastNodeIndex() || null == b.getLastNodeIndex()) {
                    return 0;
                }

                return a.getLastNodeIndex() - b.getLastNodeIndex();
            });

            // 是数组的节点也循环下
            valueList.forEach(YamlNode::resortValue);
        }
    }
}
