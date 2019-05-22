package com.simon.ocean;

import static com.simon.ocean.Out.*;

import org.junit.Test;

/**
 * Java的加密
 * @author zhouzhenyong
 * @since 2019/2/22 上午11:20
 */
public class EncryptDemo {

    @Test
    public void test1(){
        // 50e721e49c013f00c62cf59f2163542a9d8df02464efeb615d31051b0fddc326
        show(EncryptUtil.SHA256("w"));
        // aa66509891ad28030349ba9581e8c92528faab6a34349061a44b6f8fcd8d6877a67b05508983f12f8610302d1783401a07ec41c7e9ebd656de34ec60d84d9511
        show(EncryptUtil.SHA512("w"));
        // f1290186a5d0b1ceab27f4e77c0c5d68
        show(EncryptUtil.MD5("w"));
    }
}
