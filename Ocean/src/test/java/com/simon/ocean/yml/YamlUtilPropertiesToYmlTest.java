package com.simon.ocean.yml;

import com.simon.ocean.FileUtil;
import com.simon.ocean.YamlUtil;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author shizi
 * @since 2020/10/10 5:20 下午
 */
public class YamlUtilPropertiesToYmlTest {

    /**
     * 基本测试
     *
     * a.b.c.d.e=1
     * a.b1.c1.d1.e1=1
     */
    @SneakyThrows
    @Test
    public void propertiesToYmlBaseTest() {
        String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/base.properties");
        String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/base.yml");

        //a:
        //  b:
        //    c:
        //      d:
        //        e: 1
        //  b1:
        //    c1:
        //      d1:
        //        e1: 1
        Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
    }

    /**
     * 数组测试1
     *
     * a.b.c[0].d=1
     * a.b.c[1].e=2
     * a.b.c[2].e=3
     * a.b.d.e=4
     */
    @SneakyThrows
    @Test
    public void propertiesToYmlArrayTest1() {
        String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/array1.properties");
        String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/array1.yml");
        //a:
        //  b:
        //    c:
        //      - d: 1
        //      - e: 2
        //      - e: 3
        //    d:
        //      e: 4
        Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
    }

    /**
     * 数组测试2
     *
     * a.b.c[0].d=1
     * a.b.c[0].e=2
     * a.b.c[0].f=3
     * a.b.c[1].d=4
     * a.b.c[1].e=5
     * a.b.c[1].f=6
     * a.b.d.e=7
     */
    @SneakyThrows
    @Test
    public void propertiesToYmlArrayTest2() {
        String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/array2.properties");
        String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/array2.yml");
        //a:
        //  b:
        //    c:
        //      - d: 1
        //        e: 2
        //        f: 3
        //      - d: 4
        //        e: 5
        //        f: 6
        //    d:
        //      e: 7
        Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
    }

    /**
     * 数组测试3：多级数组
     *
     * a.b.c[0].d=1
     * a.b.c[1].e[0]=2
     * a.b.c[1].e[1]=3
     * a.b.c[2].e[0]=4
     * a.b.d.e=5
     */
    @SneakyThrows
    @Test
    public void propertiesToYmlArrayTest3() {
        String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/array3.properties");
        String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/array3.yml");
        //a:
        //  b:
        //    c:
        //      - d: 1
        //      - e:
        //        - 2
        //        - 3
        //      - e:
        //        - 4
        //    d:
        //      e: 5
        Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
    }

    /**
     * 数组测试4
     *
     * a.b.c[0]=1
     */
    @SneakyThrows
    @Test
    public void propertiesToYmlArrayTest4() {
        String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/array4.properties");
        String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/array4.yml");
        //a:
        //  b:
        //    c:
        //      - 1
        Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
    }

    /**
     * 数组测试5
     *
     * a.b.c[0].d.e=1
     * a.b.c[0].d.f=2
     * a.b.c[1].d.e.f=3
     * a.b.c[2].e=4
     * a.b.c[3]=5
     * a.b.d.e=6
     */
    @SneakyThrows
    @Test
    public void propertiesToYmlArrayTest5() {
        String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/array5.properties");
        String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/array5.yml");
        // a:
        //  b:
        //    c:
        //      - d:
        //          e: 1
        //          f: 2
        //      - d:
        //          e:
        //            f: 3
        //      - e: 4
        //      - 5
        //    d:
        //      e: 6
        Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
    }

    /**
     * 数组测试6
     *
     * a.b.e[0]=2
     * a.b.d.e=3
     */
    @SneakyThrows
    @Test
    public void propertiesToYmlArrayTest6() {
        String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/array6.properties");
        String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/array6.yml");
        //a:
        //  b:
        //    e:
        //      - 2
        //    d:
        //      e: 3
        Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
    }

    /**
     * 数组测试7：数组（带字符的）测试
     *
     * knowledge.init.knowledgeTitles[0].kdTitle=听不清
     * knowledge.init.knowledgeTitles[0].keyWords=[你说什么，没听清，听不清楚，再说一遍]
     * knowledge.init.knowledgeTitles[0].question=[没听懂，听不清楚]
     * knowledge.init.knowledgeTitles[1].kdInfos[0]=你好
     * knowledge.init.knowledgeTitles[1].kdInfos[1]=hello
     * knowledge.init.knowledgeTitles[1].kdInfos[2]=hi
     */
    @SneakyThrows
    @Test
    public void propertiesToYmlArrayTest7() {
        String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/array7.properties");
        String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/array7.yml");

        //knowledge:
        //  init:
        //    knowledgeTitles:
        //      - kdTitle: 听不清
        //        keyWords: '[你说什么，没听清，听不清楚，再说一遍]'
        //        question: '[没听懂，听不清楚]'
        //      - kdInfos:
        //        - 你好
        //        - hello
        //        - hi
        Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
    }

    /**
     * 多行数据测试
     *
     * isc.log.hosts=root:dell@123:10.30.30.33:22\
     * root:dell@123:10.30.30.34:22\
     * root:dell@123:10.30.30.35:22
     */
    @SneakyThrows
    @Test
    public void propertiesToYmlMultiLineTest() {
        String propertiesContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/properties/multi_line.properties");
        String ymlContent = FileUtil.readFromResource(YamlUtilPropertiesToYmlTest.class, "/yml/multi_line.yml");

        //isc:
        //  log:
        //    hosts: |
        //      root:dell@123:10.30.30.33:22
        //      root:dell@123:10.30.30.34:22
        //      root:dell@123:10.30.30.35:22
        Assert.assertEquals(ymlContent.trim(), YamlUtil.propertiesToYml(propertiesContent).trim());
    }
}
