package com.simon.ocean;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhouzhenyong
 * @since 2019/2/16 下午6:03
 */
public class YmlUtilDemo {

    /**
     * 从class路径读取yml配置为Map结构
     */
    @Test
    public void test1(){
        String path = "/property/test.yml";
        Assert.assertEquals("{nihao={haode=123, ena=keyi}}", YmlUtil.ymlToMapFromClassPath(path).toString());
    }

    /**
     * 从绝对路径读取yml配置为Map结构
     */
    @Test
    public void test2(){
        String path = "/Users/zhouzhenyong/project/private/Heimdallr/java/src/main/resources/property/test.yml";
        Assert.assertEquals("{nihao={haode=123, ena=keyi}}", YmlUtil.ymlToMapFromAbsolutePath(path).toString());
    }

    /**
     * 从class路径读取yml配置为Properties格式
     */
    @Test
    public void test3(){
        String path = "/property/test.yml";
        Assert.assertEquals("{nihao.haode=123, nihao.ena=keyi}", YmlUtil.ymlToPropertiesFromClassPath(path).toString());
    }

    /**
     * 从绝对路径读取yml配置为Properties格式
     */
    @Test
    public void test4(){
        String path = "/Users/zhouzhenyong/project/private/Heimdallr/java/src/main/resources/property/test.yml";
        Assert.assertEquals("{nihao.haode=123, nihao.ena=keyi}", YmlUtil.ymlToPropertiesFromAbsolutePath(path).toString());
    }

    /**
     * yml文件转换为Map格式
     */
    @Test
    @SneakyThrows
    public void test5(){
        String content = FileUtil.readFromResource(YmlUtilDemo.class, "/property/test.yml");
        Assert.assertEquals("{nihao={haode=123, ena=keyi}}", YmlUtil.ymlToMap(content).toString());
    }

    /**
     * yml文件转换为Properties格式
     */
    @Test
    @SneakyThrows
    public void test6(){
        String content = FileUtil.readFromResource(YmlUtilDemo.class, "/property/test.yml");
        Assert.assertEquals("{nihao.haode=123, nihao.ena=keyi}", YmlUtil.ymlToProperties(content).toString());
    }
}
