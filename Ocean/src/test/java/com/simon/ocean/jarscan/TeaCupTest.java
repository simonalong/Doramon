package com.simon.ocean.jarscan;

import static com.simon.ocean.Out.*;

import com.simon.ocean.jarscan.TeaCup;
import java.lang.reflect.Field;
import lombok.SneakyThrows;
import org.junit.Test;

/**
 * @author zhouzhenyong
 * @since 2019/5/22 下午3:22
 */
public class TeaCupTest {

    @Test
    @SneakyThrows
    public void test1() {
        TeaCup cup = TeaCup.getInstance();
        String filePath = "/Users/zhouzhenyong/project/private/King/king-admin/target/king-admin.jar";
        // 先载入文件
        cup.read(filePath);

        // 然后再读取文件中的类
        Class tClass = cup.loadClass("com.simon.king.admin.view.AccountEntity");
        Field field = tClass.getDeclaredField("type");
        show(field.getName());
    }
}
