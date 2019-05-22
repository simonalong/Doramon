package com.simon.ocean;

import static com.simon.ocean.Out.*;

import org.junit.Test;

/**
 * @author zhouzhenyong
 * @since 2019/2/19 下午2:45
 */
public class FileUtilDemo {

    @Test
    public void test1(){
        show(FileUtil.getFilePostfix("/Users/zhouzhenyong/tem/test2", "menu"));
        show(FileUtil.getFilePostfix("/Users/zhouzhenyong/tem/test3/", "menu"));
    }
}
