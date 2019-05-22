package com.simon.ocean;

import static com.simon.ocean.Out.*;

import org.junit.Test;

/**
 * @author zhouzhenyong
 * @since 2019/2/19 上午11:37
 */
public class PropertiesUtilDemo {

    @Test
    public void test1(){
        String data = "tem.nihao.ok=123\ntem.ena='222'";
        show(PropertiesUtil.propertyToMap(data));
    }

    @Test
    public void test2(){
        show(PropertiesUtil.propertiesToMapFromClassPath("/property/app.properties"));
    }

    @Test
    public void test3(){
        show(PropertiesUtil.propertiesToMapFromAbsolutePath("/Users/zhouzhenyong/project/private/Heimdallr/java/src/main/resources/property/app.properties"));
    }
}
