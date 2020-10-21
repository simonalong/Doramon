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

    @SneakyThrows
    @Test
    public void mapAndYamlBaseTest() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/base.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    @SneakyThrows
    @Test
    public void mapAndYamlBase1Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/base1.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    @SneakyThrows
    @Test
    public void mapAndYamlArray1Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/array1.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    @SneakyThrows
    @Test
    public void mapAndYamlArray2Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/array2.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    @SneakyThrows
    @Test
    public void mapAndYamlArray3Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/array3.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    @SneakyThrows
    @Test
    public void mapAndYamlArray4Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/array4.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    @SneakyThrows
    @Test
    public void mapAndYamlArray5Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/array5.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    @SneakyThrows
    @Test
    public void mapAndYamlArray6Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/array6.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    @SneakyThrows
    @Test
    public void mapAndYamlArray7Test() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/array7.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }

    @SneakyThrows
    @Test
    public void mapAndYamlMultiLineTest() {
        String ymlContent = FileUtil.readFromResource(YamlUtilMapAndYamlTest.class, "/map/multi_line.yml");

        Map<String, Object> dataMap = YamlUtil.yamlToMap(ymlContent);
        String ymlData = YamlUtil.mapToYaml(dataMap);
        Assert.assertEquals(ymlContent, ymlData);
    }
}
