package com.simon.ocean;

import static com.simon.ocean.Out.*;

import java.text.SimpleDateFormat;
import lombok.SneakyThrows;
import org.junit.Test;

/**
 * @author zhouzhenyong
 * @since 2019/3/9 下午10:04
 */
public class IdCardParserDemo {

    /**
     * 对象方法的所有用法
     */
    @Test
    @SneakyThrows
    public void testParser(){
        String idCard = "410928199102226311";
        // 获取单例对象
        IdCardParser helper = IdCardParser.getInstance();
        // 获取全国数据省市对应图
        IdCardParser.initFromContent(FileUtil.readFromResource(this.getClass(), "/data/idAddress.json"));
        helper.setIdCard(idCard);

        // 身份证号的可用性：true
        show(helper.valid());
        // 解析地址，前提是函数initFromContent 调用，并将省市数据注入进去；返回：浙江省嘉兴市海盐县
        show(helper.parseAddress());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 解析生日信息：返回：1991-02-22 00:00:00
        show(format.format(helper.parseBirthday()));

        // 解析性别：男，女，未知；返回：男
        show(helper.parseGender());
        // 解析星座：返回：白羊座
        show(helper.parseConstellation());
        // 解析年龄：28
        show(helper.parseAge());
    }

    /**
     * 对象的可用性
     */
    @Test
    @SneakyThrows
    public void testObjectIdValid(){
        // 获取单例对象
        IdCardParser helper = IdCardParser.getInstance();
        helper.setIdCard("410928199102226311");
        show(helper.valid());
    }

    /**
     * 解析地址
     */
    @Test
    @SneakyThrows
    public void testObjectIdAddress(){
        // 获取单例对象
        IdCardParser helper = IdCardParser.getInstance();
        helper.setIdCard("410928199102226311");
        show(helper.parseAddress());
    }

    /**
     * 解析生日信息
     */
    @Test
    @SneakyThrows
    public void testObjectIdBirth(){
        // 获取单例对象
        IdCardParser helper = IdCardParser.getInstance();
        helper.setIdCard("410928199102226311");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        show(format.format(helper.parseBirthday()));
    }

    /**
     * 对象方式解析身份证号的男女
     */
    @Test
    @SneakyThrows
    public void testObjectIdGender(){
        // 获取单例对象
        IdCardParser helper = IdCardParser.getInstance("410928199102226311");
        show(helper.parseGender());
    }

    /**
     * 对象方式解析身份证号的星座
     */
    @Test
    @SneakyThrows
    public void testObjectIdConstellation(){
        // 获取单例对象
        IdCardParser helper = IdCardParser.getInstance("410928199102226311");
        show(helper.parseConstellation());
    }

    /**
     * 对象方式解析身份证号的年龄
     */
    @Test
    @SneakyThrows
    public void testObjectIdAge(){
        // 获取单例对象
        IdCardParser helper = IdCardParser.getInstance("410928199105226311");
        show(helper.parseAge());
    }

    /**
     * 静态方法解析的所有用法
     */
    @Test
    @SneakyThrows
    public void testStatic(){
        String idCard = "330424199102226311";
        // 获取全国数据省市对应图
        IdCardParser.initFromContent(FileUtil.readFromResource(this.getClass(), "/data/idAddress.json"));

        // 身份证号的可用性：true
        show(IdCardParser.valid(idCard));

        // 获取全国数据省市对应图
        IdCardParser.initFromContent(FileUtil.readFromResource(this.getClass(), "/data/idAddress.json"));
        // 解析地址，前提是函数initFromContent 调用，并将省市数据注入进去；返回：浙江省嘉兴市海盐县
        show(IdCardParser.parseAddress(idCard));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 解析生日信息：返回：1991-02-22 00:00:00
        show(format.format(IdCardParser.parseBirthday(idCard)));
        // 解析性别：男，女，未知；返回：男
        show(IdCardParser.parseGender(idCard));
    }

    /**
     * 对象方式解析身份证号的男女
     */
    @Test
    @SneakyThrows
    public void testStaticIdValid(){
        // 身份证号的可用性：true
        show(IdCardParser.valid("330424199102226311"));
    }

    /**
     * 对象方式解析身份证号的男女
     */
    @Test
    @SneakyThrows
    public void testStaticIdAddress(){
        // 获取全国数据省市对应图
        IdCardParser.initFromContent(FileUtil.readFromResource(this.getClass(), "/data/idAddress.json"));
        // 解析地址，前提是函数initFromContent 调用，并将省市数据注入进去；返回：浙江省嘉兴市海盐县
        show(IdCardParser.parseAddress("330424199102226311"));
    }

    @Test
    @SneakyThrows
    public void testStaticIdBirth(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 解析生日信息：返回：1991-02-22 00:00:00
        show(format.format(IdCardParser.parseBirthday("330424199102226311")));
    }

    @Test
    @SneakyThrows
    public void testStaticIdGender(){
        // 解析性别：男，女，未知；返回：男
        show(IdCardParser.parseGender("330424199102226311"));
    }

    /**
     * 对象方式解析身份证号的星座
     */
    @Test
    @SneakyThrows
    public void testStaticIdConstellation(){
        // 获取单例对象
        show(IdCardParser.parseConstellation("410928199102226311"));
    }

    /**
     * 对象方式解析身份证号的年龄
     */
    @Test
    @SneakyThrows
    public void testStaticIdAge(){
        // 获取单例对象
        show(IdCardParser.parseAge("410928199105226311"));
    }
}
