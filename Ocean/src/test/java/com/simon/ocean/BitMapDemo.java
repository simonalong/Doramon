package com.simon.ocean;


import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhouzhenyong
 * @since 2018/11/3 下午7:22
 */
public class BitMapDemo {

    /**
     * 测试插入后是否包含
     */
    @Test
    public void test4() {
        BitMap b = BitMap.getInstance();
        b.setMaxValue(32);
        int a = 4;
        b.insert(33);
        Assert.assertTrue(b.contain(a));
    }

    @Test
    public void test5() {
        BitMap b = BitMap.getInstance();
        // 设置最大值
        b.setMaxValue(32);
        // 超过最大尺寸会报自定义的异常：SizeOutOfBoundsException
        // b.insert(33);
        // 重新设置最大值
        b.setMaxValue(54);

        // 插入成功
        b.insert(33);
        Assert.assertTrue(b.contain(33));

        b.insert(4);
        Assert.assertEquals(2, b.count());
        Assert.assertTrue(b.contain(4));
    }
}
