package com.simon.ocean;

import com.alibaba.fastjson.JSON;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
     * 换行符
     */
    private final String PROPERTY_NEW_LINE = "\n";
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
     * yml的value换行符
     */
    private final String YML_NEW_LINE_DOM = "|\n";
    private final Pattern rangePattern = Pattern.compile("^(.*)\\[(\\d*)\\]$");

    public boolean isYml(String ymlContent) {
        if (null == ymlContent || "".equals(ymlContent)) {
            return false;
        }
        try {
            Yaml yaml = new Yaml();
            yaml.loadAs(ymlContent, Map.class);
            return true;
        } catch (YAMLException e) {
            log.error("不是yml类型，因为异常：", e);
            return false;
        }
    }

    public boolean isProperties(String propertiesContent) {
        if (null == propertiesContent || "".equals(propertiesContent)) {
            return false;
        }
        return isYml(propertiesToYml(propertiesContent));
    }

    public boolean isJson(String jsonContent) {
        try {
            JSON.parseObject(jsonContent);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * yml格式转换到properties
     */
    public String ymlToProperties(String ymlContent) {
        try {
            if (null == ymlContent || "".equals(ymlContent)) {
                return null;
            }

            List<String> propertiesList = new ArrayList<>();
            Map<String, String> remarkMap = new HashMap<>();

            Yaml yaml = new Yaml();
            Map<?, ?> valueMap = yaml.loadAs(ymlContent, Map.class);

            // 读取yml的注释
            ymlToRemarkMap(remarkMap, com.amihaiemil.eoyaml.Yaml.createYamlInput(ymlContent).readYamlMapping(), "");
            formatYmlToProperties(propertiesList, remarkMap, valueMap, "");
            return propertiesList.stream().filter(e-> null != e && !"".equals(e)).reduce((a, b) -> a + PROPERTY_NEW_LINE + b).orElse("");
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
        properties.forEach((k, v) -> {
            stringBuilder.append(k).append("=").append(v).append(PROPERTY_NEW_LINE);
        });
        return propertiesToYml(stringBuilder.toString());
    }

    /**
     * properties 转换到 yml
     */
    public String propertiesToYml(String propertiesContent) {
        if (null == propertiesContent || "".equals(propertiesContent)) {
            return null;
        }
        try {
            List<String> ymlLineList = new ArrayList<>();
            List<String> propertiesLineWordList = getPropertiesItemLineList(propertiesContent);

            List<YmlNode> ymlNodes = new ArrayList<>();
            StringBuilder projectRemark = new StringBuilder();
            StringBuilder remark = new StringBuilder();
            for (String line : propertiesLineWordList) {
                line = line.trim();
                if (!"".equals(line)) {
                    if (line.startsWith("#")) {
                        if(0 != remark.length()){
                            projectRemark.append(remark.toString());
                            remark.delete(0, remark.length());
                        }
                        remark.append(line);
                        continue;
                    }
                    int index = line.indexOf("=");
                    String key = line.substring(0, index);
                    String value = line.substring(index + 1);
                    // 对于yml中换行的添加|用于保留换行
                    if (value.contains("\n")) {
                        value = YML_NEW_LINE_DOM + value;
                    }

                    final List<String> lineWordList = new ArrayList<>(Arrays.asList(key.split("\\.")));
                    wordToNode(lineWordList, ymlNodes, null, false, null, appendSpaceForArrayValue(value), projectRemark.toString(), remark.toString());

                    // 删除本地保留
                    remark.delete(0, remark.length());
                    projectRemark.delete(0, projectRemark.length());
                }
            }
            formatPropertiesToYml(ymlLineList, ymlNodes, false, "");
            return ymlLineList.stream().reduce((a, b) -> a + "\n" + b).orElse("") + "\n";
        } catch (Throwable e) {
            log.error("propertiesToYml error, propertiesContent={}", propertiesContent);
            throw new RuntimeException("properties 转换到 yml异常", e);
        }
    }

    public List<String> getPropertiesItemLineList(String propertiesContent) {
        if (null == propertiesContent) {
            return Collections.emptyList();
        }
        String[] lineList = propertiesContent.split(PROPERTY_NEW_LINE);
        List<String> itemLineList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : lineList) {
            // 处理多行数据
            if (line.endsWith("\\")) {
                stringBuilder.append(line).append("\n");
            } else {
                stringBuilder.append(line).append("\n");
                itemLineList.add(stringBuilder.toString());
                stringBuilder.delete(0, stringBuilder.length());
            }
        }
        return itemLineList;
    }

    /**
     * yml 转 map
     * <p>
     * 注意：其中yml.load返回的map，key会根据类做匹配，而不是String，这里进行转换一层
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Map<String, Object> ymlToMap(String ymlContent) {
        if (null == ymlContent || "".equals(ymlContent)) {
            return new HashMap<>();
        }
        try {
            Map<String, Object> resultMap = new HashMap<>();
            Yaml yml = new Yaml();
            Map result = yml.load(ymlContent);
            Set<Map.Entry<?, ?>> entrySet = result.entrySet();
            for (Map.Entry<?, ?> entry : entrySet) {
                resultMap.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            return resultMap;
        } catch (Throwable e) {
            log.error("ymlToMap error, ymlContent={}", ymlContent, e);
            throw new RuntimeException("yml 转换到 map 异常", e);
        }
    }

    /**
     * map 转 yml
     */
    public String mapToYml(Map<String, Object> mapData) {
        if (null == mapData || mapData.isEmpty()) {
            return null;
        }
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
        if (null == ymlContent || "".equals(ymlContent)) {
            return null;
        }
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
        if (null == jsonContent || "".equals(jsonContent)) {
            return null;
        }
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
        if (null == ymlContent || "".equals(ymlContent)) {
            return Collections.emptyList();
        }
        try {
            String propertiesContent = ymlToProperties(ymlContent);
            return getPropertiesItemLineList(propertiesContent).stream().map(e -> {
                int index = e.indexOf("=");
                String key = e.substring(0, index);
                String value = e.substring(index + 1);
                return new AbstractMap.SimpleEntry<>(key, value);
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
        if (null == kvStringList || kvStringList.isEmpty()) {
            return null;
        }
        try {
            String propertiesContent = kvStringList.stream().map(e -> e.getKey() + "=" + e.getValue()).reduce((a, b) -> a + "\n" + b).orElse("");
            return propertiesToYml(propertiesContent);
        } catch (Throwable e) {
            log.error("kvListToYml error, kvStringList={}", kvStringList);
            throw new RuntimeException("kv-list 转换到 yml 异常", e);
        }
    }


    public String kvToYml(String key, String value, ConfigValueTypeEnum valueTypeEnum) {
        if (null == key || "".equals(key)) {
            return null;
        }
        try {
            return propertiesToYml(kvToProperties(key, value, valueTypeEnum));
        } catch (Throwable e) {
            log.error("kvToYml error, key={}, value={}, valueType={}", key, value, valueTypeEnum);
            throw new RuntimeException("kv 转换到 yml 异常", e);
        }
    }

    public Map<String, Object> kvToMap(String key, String value, ConfigValueTypeEnum valueTypeEnum) {
        if (null == key || "".equals(key)) {
            return null;
        }
        try {
            Map<String, Object> dataMap = new HashMap<>();
            switch (valueTypeEnum) {
                case YML:
                    return ymlToMap(value);
                case JSON:
                    return JSON.parseObject(value);
                case PROPERTIES:
                case STRING:
                    dataMap.put(key, value);
                    return dataMap;
                default:
                    return dataMap;
            }
        } catch (Throwable e) {
            log.error("kvToProperties error, key={}, value={}, valueType={}", key, value, valueTypeEnum.name(), e);
            throw new RuntimeException("kv 转换到 properties 异常", e);
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
        if (null == key || "".equals(key)) {
            return null;
        }
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
                case PROPERTIES:
                    propertiesValue = value;
                    break;
                case STRING:
                    if (value.contains(":") || value.contains("=")) {
                        return key + "=" + "'" + value + "'";
                    } else {
                        return key + "=" + value;
                    }

                default:
                    break;
            }

            propertiesValue = getPropertiesItemLineList(propertiesValue).stream().map(e -> {
                int index = e.indexOf("=");
                String keyTem = e.substring(0, index).trim();
                String valueTem = e.substring(index + 1).trim();
                return key + DOT + keyTem + "=" + valueTem;
            }).reduce((a, b) -> a + PROPERTY_NEW_LINE + b).orElse("");
            return propertiesValue;
        } catch (Throwable e) {
            log.error("kvToProperties error, key={}, value={}, valueType={}", key, value, valueTypeEnum.name(), e);
            throw new RuntimeException("kv 转换到 properties 异常", e);
        }
    }

    /**
     * 将key转换为yml节点
     *
     * @param lineWordList 待转换的key，比如{@code k1.k2.k3=123}
     * @param nodeList 已经保存的节点数据
     * @param lastNodeArrayFlag 上一个节点是否数组类型
     * @param index 索引下标
     * @param value 解析的值
     * @param remark 当前value对应的注释
     */
    private void wordToNode(List<String> lineWordList, List<YmlNode> nodeList, YmlNode parentNode, Boolean lastNodeArrayFlag, Integer index, String value, String projectRemark, String remark) {
        if (lineWordList.isEmpty()) {
            if (lastNodeArrayFlag) {
                YmlNode node = new YmlNode();
                node.setValue(value);
                node.setRemark(remark);
                nodeList.add(node);
            }
        } else {
            String nodeName = lineWordList.get(0);

            Map.Entry<String, Integer> nameAndIndex = peelArray(nodeName);
            nodeName = nameAndIndex.getKey();
            Integer nextIndex = nameAndIndex.getValue();

            YmlNode node = new YmlNode();
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
                for (YmlNode ymlNode : nodeList) {
                    //如果节点名称已存在，则递归添加剩下的数据节点
                    if (nodeName.equals(ymlNode.getName()) && ymlNode.getArrayFlag()) {
                        Integer ymlNodeIndex = ymlNode.getLastNodeIndex();
                        if (null == ymlNodeIndex || index.equals(ymlNodeIndex)) {
                            hasEqualsName = true;
                            wordToNode(lineWordList, ymlNode.getValueList(), node.getParent(), true, nextIndex, appendSpaceForArrayValue(value), null, remark);
                        }
                    }
                }
                //如果遍历结果为节点名称不存在，则递归添加剩下的数据节点，并把新节点添加到上级ymlTree的子节点中
                if (!hasEqualsName) {
                    wordToNode(lineWordList, node.getValueList(), node.getParent(), true, nextIndex, appendSpaceForArrayValue(value), null,remark);
                    nodeList.add(node);
                }
            } else {
                boolean hasEqualsName = false;
                //遍历查询节点是否存在
                for (YmlNode ymlNode : nodeList) {
                    if (!lastNodeArrayFlag) {
                        //如果节点名称已存在，则递归添加剩下的数据节点
                        if (nodeName.equals(ymlNode.getName())) {
                            hasEqualsName = true;
                            wordToNode(lineWordList, ymlNode.getChildren(), ymlNode, false, nextIndex, appendSpaceForArrayValue(value), null,remark);
                        }
                    } else {
                        //如果节点名称已存在，则递归添加剩下的数据节点
                        if (nodeName.equals(ymlNode.getName())) {
                            Integer ymlNodeIndex = ymlNode.getLastNodeIndex();
                            if (null == ymlNodeIndex || index.equals(ymlNodeIndex)) {
                                hasEqualsName = true;
                                wordToNode(lineWordList, ymlNode.getChildren(), ymlNode, true, nextIndex, appendSpaceForArrayValue(value), null,remark);
                            }
                        }
                    }
                }
                //如果遍历结果为节点名称不存在，则递归添加剩下的数据节点，并把新节点添加到上级ymlTree的子节点中
                if (!hasEqualsName) {
                    wordToNode(lineWordList, node.getChildren(), node, false, nextIndex, appendSpaceForArrayValue(value), null,remark);
                    nodeList.add(node);
                }
            }
        }
    }

    /**
     * 获取yml中的注释
     *
     * @param remarkMap 解析后填充的注释map：key为a.b.c.d，value为对应的注释，去除掉前缀#后的数据
     * @param mapping yml解析后数据
     * @param prefix 前缀
     */
    private void ymlToRemarkMap(Map<String, String> remarkMap, YamlMapping mapping, String prefix) {
        if(null == mapping) {
            return;
        }
        for (YamlNode node : mapping.keys()) {
            String nodeName = node.asScalar().value();
            String remark = mapping.value(node).comment().value();

            if (null != remark && !"".equals(remark)) {
                remarkMap.put(wrapKey(prefix, nodeName), remark);
            }

            ymlToRemarkMap(remarkMap, mapping.yamlMapping(node), wrapKey(prefix, nodeName));
        }
    }

    private String wrapKey(String prefix, String value) {
        if (null != prefix && !"".equals(prefix)) {
            return prefix + "." + value;
        }
        return value;
    }

    /**
     * 解析节点名字，为数组则返回数组名和节点下标
     * <p>
     *     name.test[0] 将test和0进行返回
     * @param nodeName 界面的名字
     * @return 如果是数组，则将数组名和解析后的下标返回
     */
    private Map.Entry<String, Integer> peelArray(String nodeName) {
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

        return new AbstractMap.SimpleEntry<>(name, index);
    }

    /**
     * 将yml对应的这种value进行添加前缀空格，其中value为key1对应的value
     * {@code
     * test:
     *   key1: |
     *     value1
     *     value2
     *     value3
     * }
     * 对应的值
     * {@code
     * |
     *   value1
     *   value2
     *   value3
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
     *   value1
     *   value2
     *   value3
     * }
     */
    private String appendSpaceForArrayValue(String value) {
        if (!value.startsWith(YML_NEW_LINE_DOM)) {
            return value;
        }
        String valueTem = value.substring(YML_NEW_LINE_DOM.length());
        return YML_NEW_LINE_DOM + Arrays.stream(valueTem.split("\\n"))
            .map(e -> {
                String tem = e;
                if (e.endsWith("\\")) {
                    tem = e.substring(0, e.length() - 1);
                }
                return INDENT_BLANKS + tem;
            })
            .reduce((a, b) -> a + "\n" + b)
            .orElse(valueTem);
    }

    private void formatPropertiesToYml(List<String> ymlLineList, List<YmlNode> ymlNodes, Boolean lastNodeArrayFlag, String blanks) {
        Integer beforeNodeIndex = null;
        String equalSign;
        for (YmlNode ymlNode : ymlNodes) {
            String value = ymlNode.getValue();
            String remark = ymlNode.getRemark();

            equalSign = SIGN_SEMICOLON;
            if (null == value || "".equals(value)) {
                value = "";
            } else {
                equalSign = SIGN_SEMICOLON + " ";
            }
            ymlNode.resortValue();

            String name = ymlNode.getName();
            if (lastNodeArrayFlag) {
                if (null == name) {
                    ymlLineList.add(blanks + ARRAY_BLANKS + stringValueWrap(value));
                } else {
                    if(null != beforeNodeIndex && beforeNodeIndex.equals(ymlNode.getLastNodeIndex())) {
                        ymlLineList.add(blanks + INDENT_BLANKS + name + equalSign + stringValueWrap(value));
                    } else {
                        ymlLineList.add(blanks + ARRAY_BLANKS + name + equalSign + stringValueWrap(value));
                    }
                }
                beforeNodeIndex = ymlNode.getLastNodeIndex();
            } else {
                // 父节点为空，表示，当前为顶层
                if (null == ymlNode.getParent()) {
                    ymlLineList.add(blanks + getRemarkProject(ymlNode.getProjectRemark()));
                }

                // 自己节点为数组，则添加对应的注释
                if (ymlNode.getArrayFlag()) {
                    if (null != remark && !"".equals(remark)) {
                        ymlLineList.add(blanks + remark);
                    }
                }
                ymlLineList.add(blanks + name + equalSign + stringValueWrap(value, remark));
            }

            if (ymlNode.getArrayFlag()) {
                if (lastNodeArrayFlag) {
                    formatPropertiesToYml(ymlLineList, ymlNode.getValueList(), true, INDENT_BLANKS + INDENT_BLANKS + blanks);
                } else {
                    formatPropertiesToYml(ymlLineList, ymlNode.getValueList(), true, INDENT_BLANKS + blanks);
                }
            } else {
                if (lastNodeArrayFlag) {
                    formatPropertiesToYml(ymlLineList, ymlNode.getChildren(), false, INDENT_BLANKS + INDENT_BLANKS + blanks);
                } else {
                    formatPropertiesToYml(ymlLineList, ymlNode.getChildren(), false, INDENT_BLANKS + blanks);
                }
            }
        }
    }

    private void appendRemark(List<String> ymlLineList, String blanks, String remark) {
        if (null == remark || "".endsWith(remark)) {
            return;
        }
        for (String remarkItem : remark.split("\n")) {
            ymlLineList.add(blanks + remarkItem);
        }
    }

    @SuppressWarnings("rawtypes")
    private void formatYmlToProperties(List<String> propertiesLineList, Map<String, String> remarkMap, Object object, String prefix) {
        if (null == object) {
            return;
        }
        if (object instanceof Map) {
            // 填充注释
            if (remarkMap.containsKey(prefix)) {
                propertiesLineList.add("# " + remarkMap.get(prefix));
            }

            Map map = (Map) object;
            Set<?> set = map.keySet();
            for (Object key : set) {
                Object value = map.get(key);
                if(null == value) {
                    value = "";
                }
                if (value instanceof Map) {
                    formatYmlToProperties(propertiesLineList, remarkMap, value, prefixWithDOT(prefix) + key);
                } else if (value instanceof Collection) {
                    Collection collection = (Collection) value;
                    if (!collection.isEmpty()) {
                        // 填充注释
                        if (remarkMap.containsKey(prefixWithDOT(prefix) + key)) {
                            propertiesLineList.add("# " + remarkMap.get(prefixWithDOT(prefix) + key));
                        }

                        Iterator<?> iterator = collection.iterator();
                        int index = 0;
                        while (iterator.hasNext()) {
                            Object valueObject = iterator.next();
                            formatYmlToProperties(propertiesLineList, remarkMap, valueObject, prefixWithDOT(prefix) + key + "[" + index + "]");
                            index = index + 1;
                        }
                    }
                } else if (value instanceof String) {
                    String valueStr = (String) value;
                    valueStr = valueStr.trim();
                    valueStr = valueStr.replace("\n", "\\\n");
                    // 填充注释
                    if (remarkMap.containsKey(prefixWithDOT(prefix) + key)) {
                        propertiesLineList.add("# " + remarkMap.get(prefixWithDOT(prefix) + key));
                    }

                    propertiesLineList.add(prefixWithDOT(prefix) + key + SIGN_EQUAL + valueStr);
                } else {
                    // 填充注释
                    if (remarkMap.containsKey(prefixWithDOT(prefix) + key)) {
                        propertiesLineList.add("# " + remarkMap.get(prefixWithDOT(prefix) + key));
                    }

                    propertiesLineList.add(prefixWithDOT(prefix) + key + SIGN_EQUAL + value);
                }
            }
        } else if (object instanceof Collection) {
            Collection collection = (Collection) object;
            if (!collection.isEmpty()) {
                // 填充注释
                if (remarkMap.containsKey(prefix)) {
                    propertiesLineList.add("# " + remarkMap.get(prefix));
                }

                Iterator<?> iterator = collection.iterator();
                int index = 0;
                while (iterator.hasNext()) {
                    Object valueObject = iterator.next();
                    formatYmlToProperties(propertiesLineList, remarkMap, valueObject, prefix + "[" + index + "]");
                    index = index + 1;
                }
            }
        } else if (object.getClass().isArray()) {
            Object[] array = (Object[]) object;
            for (int index = 0; index < array.length; index++) {
                formatYmlToProperties(propertiesLineList, remarkMap, array[index], prefix + "[" + index + "]");
            }
        } else if (object instanceof String){
            String valueObject = (String) object;
            valueObject = valueObject.replace("\n", "\\\n");
            // 填充注释
            if (remarkMap.containsKey(prefix)) {
                propertiesLineList.add("# " + remarkMap.get(prefix));
            }

            propertiesLineList.add(prefix + SIGN_EQUAL + valueObject);
        } else {
            // 填充注释
            if (remarkMap.containsKey(prefix)) {
                propertiesLineList.add("# " + remarkMap.get(prefix));
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
        if(null == value || "".equals(value)) {
            return "";
        }
        // 对数组的数据进行特殊处理
        if (value.startsWith("[") && value.endsWith("]")) {
            return "'" + value + "'";
        }
        return value;
    }

    private String stringValueWrap(String value, String remark) {
        if(null == value || "".equals(value)) {
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
            return "# " + remark.substring(1).trim();
        } else {
            return "";
        }
    }


    @Data
    class YmlNode {

        private YmlNode parent;
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
        private List<YmlNode> children = new ArrayList<>();

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
        private List<YmlNode> valueList = new ArrayList<>();

        /**
         * 将其中的value按照index下标顺序进行重拍
         */
        public void resortValue() {
            if (!arrayFlag || valueList.isEmpty() || null == lastNodeIndex) {
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
            valueList.forEach(YmlNode::resortValue);
        }
    }

    @AllArgsConstructor
    public enum ConfigValueTypeEnum {

        /**
         * yml配置
         */
        YML("yml配置"),
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

        private static final Map<Integer, YamlUtil.ConfigValueTypeEnum> indexEnumMap;
        private static final Map<String, YamlUtil.ConfigValueTypeEnum> nameEnumMap;

        static {
            indexEnumMap = Arrays.stream(YamlUtil.ConfigValueTypeEnum.values()).collect(Collectors.toMap(
                YamlUtil.ConfigValueTypeEnum::ordinal, e -> e));
            nameEnumMap = Arrays.stream(YamlUtil.ConfigValueTypeEnum.values()).collect(Collectors.toMap(
                YamlUtil.ConfigValueTypeEnum::name, e -> e));
        }

        public static YamlUtil.ConfigValueTypeEnum parse(Integer index) {
            if (!indexEnumMap.containsKey(index)) {
                throw new RuntimeException("不支持下标: " + index);
            }
            return indexEnumMap.get(index);
        }

        public static YamlUtil.ConfigValueTypeEnum parse(String name) {
            if (!nameEnumMap.containsKey(name)) {
                throw new RuntimeException("不支持name: " + name);
            }
            return nameEnumMap.get(name);
        }
    }
}
