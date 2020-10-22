package com.simon.ocean.yml;

import com.simon.ocean.FileUtil;
import com.simon.ocean.YamlUtil;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author shizi
 * @since 2020/10/21 2:06 下午
 */
public class YamlUtilMapAndYamlTest {

    /**
     * a:
     *   b:
     *     c:
     *       d:
     *         e: 1
     *   b1:
     *     c1:
     *       d1:
     *         e1: 1
     */
    @SneakyThrows
    @Test
    public void mapAndYamlBaseTest() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/base.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    /**
     * a:
     *   b:
     *     c: 1
     *   b1:
     *     c:
     *       d:
     *         e: 1
     *   b2:
     *     c1:
     *       d1:
     *         e1: 1
     *         e2: 2
     *         e3: 3
     *       d2:
     *         - 3
     *         - 3
     *         - 3
     */
    @SneakyThrows
    @Test
    public void mapAndYamlBase1Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/base1.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    /**
     * a:
     *   b:
     *     c:
     *       - d: 1
     *       - e: 2
     *       - e: 3
     *     d:
     *       e: 4
     */
    @SneakyThrows
    @Test
    public void mapAndYamlArray1Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/array1.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    /**
     * a:
     *   b:
     *     c:
     *       - d: 1
     *         e: 2
     *         f: 3
     *       - d: 4
     *         e: 5
     *         f: 6
     *     d:
     *       e: 7
     */
    @SneakyThrows
    @Test
    public void mapAndYamlArray2Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/array2.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    /**
     * a:
     *   b:
     *     c:
     *       - d: 1
     *       - e:
     *           - 2
     *           - 3
     *       - e:
     *           - 4
     *     d:
     *       e: 5
     */
    @SneakyThrows
    @Test
    public void mapAndYamlArray3Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/array3.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    /**
     * a:
     *   b:
     *     c:
     *       - 1
     */
    @SneakyThrows
    @Test
    public void mapAndYamlArray4Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/array4.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    /**
     * a:
     *   b:
     *     c:
     *       - d:
     *           e: 1
     *           f: 2
     *       - d:
     *           e:
     *             f: 3
     *       - e: 4
     *       - 5
     *     d:
     *       e: 6
     */
    @SneakyThrows
    @Test
    public void mapAndYamlArray5Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/array5.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    /**
     * a:
     *   b:
     *     e:
     *       - 2
     *     d:
     *       e: 3
     */
    @SneakyThrows
    @Test
    public void mapAndYamlArray6Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/array6.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    /**
     * knowledge:
     *   init:
     *     knowledgeTitles:
     *       - kdTitle: 听不清
     *         keyWords: '[你说什么，没听清，听不清楚，再说一遍]'
     *         question: '[没听懂，听不清楚]'
     *       - kdInfos:
     *           - 你好
     *           - hello
     *           - hi
     */
    @SneakyThrows
    @Test
    public void mapAndYamlArray7Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/array7.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    /**
     * isc:
     *   log:
     *     hosts: |
     *       root:dell@123:10.30.30.33:22
     *       root:dell@123:10.30.30.34:22
     *       root:dell@123:10.30.30.35:22
     *   name: 123
     */
    @SneakyThrows
    @Test
    public void mapAndYamlMultiLineTest() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/multi_line.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }
}
