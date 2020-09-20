package com.simon.ocean;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.stream.Collectors;

/**
 * yml与其他各种格式之间互转
 * <p>
 * <ul>
 *     <li>1.yml 转 properties</li>
 *     <li>2.properties 转 yml</li>
 *     <li>3.yml 转 json</li>
 *     <li>4.json 转 yml</li>
 *     <li>5.yml 转 map</li>
 *     <li>6.map 转 yml</li>
 *     <li>7.yml 转 kvList</li>
 *     <li>8.kvList 转 yml</li>
 * </ul>
 *
 * @author shizi
 * @since 2020/9/14 3:17 下午
 */
@Slf4j
@UtilityClass
public class YamlUtil {

    /**
     * 缩进空格
     */
    private final String INDENT_BLANKS = "  ";
    /**
     * 值连接符
     */
    private final String VALUE_LINK_SIGN_YML = ": ";
    /**
     * 值连接符
     */
    private final String VALUE_LINK_SIGN_PROPERTIES = "=";
    /**
     * 点
     */
    private final String DOT = ".";

    /**
     * yml格式转换到properties
     */
    public String ymlToProperties(String ymlContent) {
        try {
            if (null == ymlContent || "".equals(ymlContent)) {
                return null;
            }

            List<String> propertiesList = new ArrayList<>();
            Yaml yaml = new Yaml();
            Map<?, ?> map = yaml.loadAs(ymlContent, Map.class);
            format(propertiesList, map, "");
            return propertiesList.stream().reduce((a, b) -> a + "\n" + b).orElse("");
        } catch (Throwable e) {
            log.error("ymlToProperties error, ymlContent={}", ymlContent);
            throw new RuntimeException("yml 转换到 properties异常", e);
        }
    }

    /**
     * properties 转换到 yml
     */
    public String propertiesToYml(Properties properties) {
        StringBuilder stringBuilder = new StringBuilder();
        properties.forEach((k, v)->{
            stringBuilder.append(k).append("=").append(v).append("\n");
        });
        return propertiesToYml(stringBuilder.toString());
    }

    /**
     * properties 转换到 yml
     */
    public String propertiesToYml(String propertiesContent) {
        try {
            List<String> ymlLineList = new ArrayList<>();
            String[] propertiesLineWordList = propertiesContent.split("\n");

            List<YmlNode> ymlNodes = new ArrayList<>();
            for (String line : propertiesLineWordList) {
                if (!"".equals(line.trim()) && !line.trim().startsWith("#")) {
                    String[] strings = line.trim().split("=", 2);
                    String value = "";
                    if (strings.length == 2) {
                        value = strings[1].trim();
                    }
                    final List<String> lineWordList = new ArrayList<>(Arrays.asList(strings[0].trim().split("\\.")));
                    addNode(lineWordList, ymlNodes, value);
                }
            }
            format(ymlLineList, ymlNodes, "");
            return ymlLineList.stream().reduce((a, b) -> a + "\n" + b).orElse("");
        } catch (Throwable e) {
            log.error("propertiesToYml error, propertiesContent={}", propertiesContent);
            throw new RuntimeException("properties 转换到 yml异常", e);
        }
    }

    /**
     * yml 转 map
     */
    public Map<String, Object> ymlToMap(String ymlContent) {
        try {
            Yaml yml = new Yaml();
            return yml.load(ymlContent);
        } catch (Throwable e) {
            log.error("ymlToMap error, ymlContent={}", ymlContent, e);
            throw new RuntimeException("yml 转换到 map 异常", e);
        }
    }

    /**
     * map 转 yml
     */
    public String mapToYml(Map<String, Object> mapData) {
        try {
            Yaml yaml = new Yaml();
            return yaml.dumpAsMap(mapData);
        } catch (Throwable e) {
            log.error("mapToYml error, mapData={}", mapData);
            throw new RuntimeException("map 转换到 yml 异常", e);
        }
    }

    /**
     * yml 转 json
     */
    public String ymlToJson(String ymlContent) {
        try {
            return JSON.toJSONString(ymlToMap(ymlContent));
        } catch (Throwable e) {
            log.error("ymlToJson error, ymlContent={}", ymlContent);
            throw new RuntimeException("yml 转换到 json 异常", e);
        }
    }

    /**
     * json 转 yml
     */
    public String jsonToYml(String jsonContent) {
        try {
            return mapToYml(JSON.parseObject(jsonContent));
        } catch (Throwable e) {
            log.error("jsonToYml error, jsonContent={}", jsonContent);
            throw new RuntimeException("json 转换到 yml 异常", e);
        }
    }

    /**
     * yml类型转换为 k-v的集合
     */
    public List<Map.Entry<String, String>> ymlToKVList(String ymlContent) {
        try {
            String propertiesContent = ymlToProperties(ymlContent);
            return Arrays.stream(propertiesContent.split("\n")).map(e -> {
                String[] kv = e.split("=");
                return new AbstractMap.SimpleEntry<>(kv[0], kv[1]);
            }).collect(Collectors.toList());
        } catch (Throwable e) {
            log.error("ymlToKVList error, ymlContent={}", ymlContent);
            throw new RuntimeException("yml 转换到 kv-list 异常", e);
        }
    }

    /**
     * k-v的集合类型转yml
     */
    public String kvListToYml(List<Map.Entry<String, Object>> kvStringList) {
        try {
            String propertiesContent = kvStringList.stream().map(e -> e.getKey() + "=" + e.getValue()).reduce((a, b) -> a + "\n" + b).orElse("");
            return propertiesToYml(propertiesContent);
        } catch (Throwable e) {
            log.error("kvListToYml error, kvStringList={}", kvStringList);
            throw new RuntimeException("kv-list 转换到 yml 异常", e);
        }
    }


    public String kvToYml(String key, String value, ConfigValueTypeEnum valueTypeEnum) {
        try {
            return propertiesToYml(kvToProperties(key, value, valueTypeEnum));
        } catch (Throwable e) {
            log.error("kvToYml error, key={}, value={}, valueType={}", key, value, valueTypeEnum);
            throw new RuntimeException("kv 转换到 yml 异常", e);
        }
    }

    /**
     * k-v的String类型转properties
     *
     * <p>其中key可能是a.b.c这种，而value可能是各种各样的类型，我们这里通过valueType进行区分
     *
     * @param key           主键
     * @param value         待转换的值
     * @param valueTypeEnum 值的类型，0：yml，1：properties，2：json，3：string
     * @return 转换之后的yml类型
     */
    public String kvToProperties(String key, String value, ConfigValueTypeEnum valueTypeEnum) {
        try {
            // 将value对应的值先转换为properties类型，然后对key进行拼接，最后再统一转化为yml格式
            String propertiesValue = "";
            switch (valueTypeEnum) {
                case YML:
                    propertiesValue = ymlToProperties(value);
                    break;
                case JSON:
                    propertiesValue = ymlToProperties(jsonToYml(value));
                    break;
                default:
                    propertiesValue = value;
                    break;
            }

            if (valueTypeEnum.equals(ConfigValueTypeEnum.STRING)) {
                propertiesValue = key + "=" + value;
            } else {
                propertiesValue = Arrays.stream(propertiesValue.split("\n")).map(e -> {
                    String[] kv = e.split("=");
                    return key + "." + kv[0] + "=" + kv[1];
                }).reduce((a, b) -> a + "\n" + b).orElse("");
            }
            return propertiesValue;
        } catch (Throwable e) {
            log.error("kvToProperties error, key={}, value={}, valueType={}", key, value, valueTypeEnum.name(), e);
            throw new RuntimeException("kv 转换到 properties 异常", e);
        }
    }

    private void addNode(List<String> lineWordList, List<YmlNode> ymlNodes, String value) {
        if (!lineWordList.isEmpty()) {
            String first = lineWordList.get(0);

            YmlNode node = new YmlNode();
            node.setName(first);
            lineWordList.remove(0);

            //如果节点下面的子节点数量为0，则为终端节点，也就是赋值节点
            if (lineWordList.size() == 0) {
                node.setValue(value);
            }

            boolean hasEqualsName = false;
            //遍历查询节点是否存在
            for (YmlNode ymlNode : ymlNodes) {
                //如果节点名称已存在，则递归添加剩下的数据节点
                if (first.equals(ymlNode.getName())) {
                    hasEqualsName = true;
                    addNode(lineWordList, ymlNode.getChildren(), value);
                }
            }
            //如果遍历结果为节点名称不存在，则递归添加剩下的数据节点，并把新节点添加到上级ymlTree的子节点中
            if (!hasEqualsName) {
                addNode(lineWordList, node.getChildren(), value);
                ymlNodes.add(node);
            }
        }
    }

    private void format(List<String> ymlLineList, List<YmlNode> ymlNodes, String blanks) {
        for (YmlNode ymlNode : ymlNodes) {
            String value = ymlNode.getValue();
            if (null == value) {
                value = "";
            }
            ymlLineList.add(blanks + ymlNode.getName() + VALUE_LINK_SIGN_YML + value);
            format(ymlLineList, ymlNode.getChildren(), INDENT_BLANKS + blanks);
        }
    }

    private void format(List<String> propertiesLineList, Map<?, ?> map, String prefix) {
        Set<?> set = map.keySet();
        for (Object key : set) {
            Object value = map.get(key);
            if (value instanceof Map) {
                if ("".equals(prefix)) {
                    format(propertiesLineList, (Map<?, ?>) value, key.toString());
                } else {
                    format(propertiesLineList, (Map<?, ?>) value, prefix + DOT + key);
                }
            } else {
                if (value == null) {
                    value = "";
                }
                if ("".equals(prefix)) {
                    propertiesLineList.add(key + VALUE_LINK_SIGN_PROPERTIES + value);
                } else {
                    propertiesLineList.add(prefix + DOT + key + VALUE_LINK_SIGN_PROPERTIES + value);
                }
            }
        }
    }

    @Data
    class YmlNode {

        /**
         * name
         */
        private String name;
        /**
         * value
         */
        private String value;
        /**
         * 子节点
         */
        private List<YmlNode> children = new ArrayList<>();
    }
}

