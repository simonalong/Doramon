package com.simon.ocean;

import static com.simon.ocean.Out.*;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import java.nio.charset.Charset;
import org.junit.Test;

/**
 * @author zhouzhenyong
 * @since 2018/11/3 上午10:44
 */
public class BloomFilterDemo {

    @Test
    public void testBloomFilter(){
        //CharSequence 是String的父类
        BloomFilter<CharSequence> filter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("utf-8")), 10000000, 0.001F);
        filter.put("asdf");
        if(filter.mightContain("asdf")){
            System.out.println("包含");
        }else{
            System.out.println("不包含");
        }
    }

    @Test
    public void test2(){
        //实际的数据大小
        long n =  50 * 10000 * 10000l;
        //误判率
        float p = 0.01f;

        show(BloomFilterUtil.getBitsSize(n, p));
        show(BloomFilterUtil.getBitsSizeOnGByte(n, p));
        show(BloomFilterUtil.getHashNum(BloomFilterUtil.getBitsSize(n, p), n));
    }
}
