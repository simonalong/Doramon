package com.simon.ocean;

import static com.simon.ocean.Out.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * @author zhouzhenyong
 * @since 2019/5/22 下午2:48
 */
public class StringTypeUtilTest {

    @Test
    public void testJsonStr(){
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("a", "1");
        dataMap.put("b", "2");
        dataMap.put("c", "3");
        dataMap.put("d", "4");
        dataMap.put("e", "5");

        //展示格式是这样的
        //{
        //     "a":"1",
        //     "b":"2",
        //     "c":"3",
        //     "d":"4",
        //     "e":"5"
        //}
        show(StringTypeUtil.parseJson(JSON.toJSONString(dataMap)));
    }

    @Test
    public void testJson(){
        String data = "{\n"
            + "     \"a\":\"1\",\n"
            + "     \"b\":\"2\",\n"
            + "     \"c\":\"3\",\n"
            + "     \"d\":\"4\",\n"
            + "     \"e\":\"5\"\n"
            + "}";
        JSONObject jsonObject = JSON.parseObject(data);
        show(jsonObject);
    }

}
