package com.simon.ocean;

import static com.simon.ocean.Out.*;

import com.simonalong.neo.NeoMap;
import lombok.SneakyThrows;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author zhouzhenyong
 * @since 2019/2/16 下午6:03
 */
public class YamlUtilTest {

    @SneakyThrows
    @Test
    public void ymlToPropertiesTest() {
        //nihao:
        //  ok:
        //    flag: 1
        //  test: sadfokwerkwe
        String content = FileUtil.readFromResource(TimeStrUtilDemo.class, "/property/test2.yml");

        show(content);
        tab();
        // {nihao.test=sadfokwerkwe, nihao.ok.flag=1}
        show(YamlUtil.ymlToProperties(content));

        tab();
        content = "a: \n" + "  b: \n" + "    c: \n" + "      d: 22";
        show(YamlUtil.ymlToProperties(content));
    }

    @SneakyThrows
    @Test
    public void propertiesToYml() {
        String propertiesContent = FileUtil.readFromResource(TimeStrUtilDemo.class, "/property/app.properties");
        show(propertiesContent);
        tab();
        show(YamlUtil.propertiesToYml(propertiesContent));
    }

    @SneakyThrows
    @Test
    public void ymlToMap() {
        String content = FileUtil.readFromResource(TimeStrUtilDemo.class, "/property/test2.yml");
        show(content);
        tab();
        show(YamlUtil.ymlToMap(content));

        String contentStr = "a: \n" + "  b: \n" + "    c: \n" + "      d: 22";
        show(YamlUtil.ymlToMap(contentStr));
    }

    @SneakyThrows
    @Test
    public void mapToYml() {
        NeoMap dataMap = NeoMap.of();
        dataMap.put("k1", 12);
        dataMap.put("k3", "ok");

        NeoMap dataMap2 = NeoMap.of();
        dataMap2.put("next1", dataMap);
        dataMap2.put("next2", 12);

        NeoMap dataMap3 = NeoMap.of();
        dataMap3.put("k3", 123);
        dataMap3.put("k4", dataMap2);

        //k3: 123
        //k4:
        //  next1:
        //    k1: 12
        //    k3: ok
        //  next2: 12
        show(YamlUtil.mapToYml(dataMap3));
    }

    @SneakyThrows
    @Test
    public void ymlToJson() {
        String content = FileUtil.readFromResource(TimeStrUtilDemo.class, "/property/test2.yml");
        show(content);
        tab();
        show(YamlUtil.ymlToJson(content));
    }

    @SneakyThrows
    @Test
    public void jsonToYml() {
        String json = "{\"nihao\":{\"ok\":{\"tem\":123,\"ena\":\"oooo\"}},\"keyi\":{\"shiba\":{\"en\":\"women\"}},\"key\":{\"cesho\":{\"keshi\":1231,\"women\":\"shide\"}}}";
        //nihao:
        //  ok:
        //    ena: oooo
        //    tem: 123
        //keyi:
        //  shiba:
        //    en: women
        //key:
        //  cesho:
        //    keshi: 1231
        //    women: shide
        show(YamlUtil.jsonToYml(json));
    }

    @SneakyThrows
    @Test
    public void ymlToKVString() {
        String content = FileUtil.readFromResource(TimeStrUtilDemo.class, "/property/test2.yml");
        show(content);
        tab();
        // [nihao.ok.tem=123, nihao.ok.ena=oooo, keyi.shiba.en=women, key.cesho.keshi=1231, key.cesho.women=shide]
        show(YamlUtil.ymlToKVList(content));
    }

    @SneakyThrows
    @Test
    public void kvListToYml() {
        List<Map.Entry<String, Object>> kvList = new ArrayList<>();
        kvList.add(new AbstractMap.SimpleEntry<>("nihao.ok.tem", 123));
        kvList.add(new AbstractMap.SimpleEntry<>("nihao.ok.ena", "oooo"));
        kvList.add(new AbstractMap.SimpleEntry<>("keyi.shiba.en", "women"));
        kvList.add(new AbstractMap.SimpleEntry<>("key.cesho.keshi", 1231));
        kvList.add(new AbstractMap.SimpleEntry<>("key.cesho.women", "shide"));

        //nihao:
        //  ok:
        //    tem: 123
        //    ena: oooo
        //keyi:
        //  shiba:
        //    en: women
        //key:
        //  cesho:
        //    keshi: 1231
        //    women: shide
        show(YamlUtil.kvListToYml(kvList));
    }

    @SneakyThrows
    @Test
    public void kvToYml() {
        //nihao:
        //  ok:
        //    test:
        //      flag: 123
        show(YamlUtil.kvToYml("nihao", 123 + "", ConfigValueTypeEnum.STRING));
        tab();
        show(YamlUtil.kvToYml("nihao.ok.test.flag", 123 + "", ConfigValueTypeEnum.STRING));
        tab();
        //nihao:
        //  ok:
        //    test:
        //      flag:
        //        a:
        //          b:
        //            c:
        //              d: 22
        show(YamlUtil.kvToYml("nihao.ok.test.flag", "a: \n" + "  b: \n" + "    c: \n" + "      d: 22", ConfigValueTypeEnum.YML));

        NeoMap dataMap = NeoMap.of();
        dataMap.put("k1", 12);
        dataMap.put("k3", "ok");

        NeoMap dataMap2 = NeoMap.of();
        dataMap2.put("next1", dataMap);
        dataMap2.put("next2", 12);

        NeoMap dataMap3 = NeoMap.of();
        dataMap3.put("k3", 123);
        dataMap3.put("k4", dataMap2);
        tab();
        show(YamlUtil.kvToYml("nihao.ok.test.flag", dataMap3.toString(), ConfigValueTypeEnum.JSON));
        tab();
        show(YamlUtil.kvToYml("nihao.ok.test.flag", "a.b=12", ConfigValueTypeEnum.PROPERTIES));
    }

    /**
     * 所有的yml单项进行合并
     */
    @Test
    public void allYmlToOverView() {
        String k1 = "a.b";
        String v1 = "c: \n" +
            "  d: 12";

        String k2 = "a.e";
        String v2 = "e1: \n" +
            "  d1: 13";

        String properties = "";

        properties += "\n" + YamlUtil.kvToProperties(k1, v1, ConfigValueTypeEnum.YML);
        properties += "\n" + YamlUtil.kvToProperties(k2, v2, ConfigValueTypeEnum.YML);

        show(YamlUtil.propertiesToYml(properties));
    }

    @SneakyThrows
    @Test
    public void testPropertiesShowKV() {
        Properties properties = new Properties();
        String dataStr = "test.nihao=123\nk2.kk=22";
        properties.load(new ByteArrayInputStream(dataStr.getBytes(StandardCharsets.UTF_8)));
        show(properties.getProperty("k2.kk"));
        show(properties.stringPropertyNames());

        properties.forEach((key, value) -> show(key, value));

        Deque<Integer> deque = new ArrayDeque();
        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);
        deque.addFirst(4);

        deque.getLast();

        Queue<Integer> keyQueue = new ArrayBlockingQueue<Integer>(100);
        keyQueue.offer(1);
        keyQueue.offer(2);
        keyQueue.offer(3);
        keyQueue.offer(4);

        show(keyQueue.poll());
        show(keyQueue.poll());
        show(keyQueue.poll());
        show(keyQueue.poll());
        show(keyQueue.poll());
    }
}
