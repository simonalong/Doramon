package com.simon.ocean;

import static com.simon.ocean.Out.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhouzhenyong
 * @since 2019/2/22 下午2:56
 */
public class IdempotencyDemo {

    /**
     * 单个数据测试
     */
    @Test
    @SneakyThrows
    public void test1() {
        Idempotency idem = Idempotency.getInstance().setExpire(2, TimeUnit.SECONDS);
        idem.contain("aa");
        sleep(1);

        Assert.assertTrue(idem.contain("aa"));

        sleep(1);

        Assert.assertFalse(idem.contain("aa"));
    }

    /**
     * 单个数据测试
     */
    @Test
    @SneakyThrows
    public void testExpireTime() {
        Idempotency idem = Idempotency.getInstance();
        idem.setExpire(2, TimeUnit.SECONDS);

        idem.contain("aa");
        sleep(1);
        Assert.assertTrue(idem.contain("aa"));
    }

    /**
     * 单个数据测试
     */
    @Test
    @SneakyThrows
    public void testMaxValue() {
        Idempotency idem = Idempotency.getInstance();
        idem.setMaxDataSize(100 * 1000);
        idem.contain("aa");
    }

//    public void showRe(Idempotency idem, String tem) {
//        if (idem.contain(tem)) {
//            show("ok");
//        }
//        showSize(idem);
//    }

//    /**
//     * 多批量数据测试, 100个数据
//     */
//    @Test
//    public void test2() {
//
//        Idempotency idem = Idempotency.getInstance();
//        // 数据插入
//        int i = 0;
//        int size = 200 * 10000;
//        while (i < size) {
//            idem.contain("a" + i);
//            if (i < 100) {
//                showSize1(idem);
//            }
//            i++;
//        }
//
//        tab("总大小");
//        // 数据大小统计
//        show("size -- " + SizeUtil.strSizeOf(idem));
//
//        size = 400 * 10000;
//        while (i < size) {
//            idem.contain("a" + i);
//            if (i < 100) {
//                showSize1(idem);
//            }
//            i++;
//        }
//
//        tab("总大小");
//        show("size -- " + SizeUtil.strSizeOf(idem));
//
//        int j = 0;
//        while (true) {
//            showSize(idem);
//            if (j > 10) {
//                show("清理");
//            }
//            j++;
//        }
//    }
//
//    public void showSize1(Idempotency idem) {
//        show("size -- " + SizeUtil.strSizeOf(idem));
//    }
//
//    public void showSize(Idempotency idem) {
//        sleep(2);
//        show("idem    size -- " + SizeUtil.strSizeOf(idem));
//    }

    /**
     * 多个数据的一起测试
     */
    @Test
    public void test4() {
        Idempotency idem = Idempotency.getInstance();
        idem.setExpire(5, TimeUnit.SECONDS);
        int i = 0;
        while (true) {
            judge(idem);
            sleep(2);
            if (i >= 12) {
                break;
            }
            i++;
        }

    }

    private void judge(Idempotency idem) {
        if (idem.contain("a", "b", 12)) {
            show("包含");
        } else {
            show("不包含");
        }
    }

    /**
     * 第三方存储机构的回调 增加，删除，为空，
     */
    @Test
    public void test3D() {
        Idempotency idem = Idempotency.getInstance();
        idem.setMaxDataSize(10);

        Map<String, Long> otherMap = new ConcurrentHashMap<>();

        // 增加
        idem.registerInsertHook((key, expireTime) -> {
            show("三方增加数据：key=" + key + ", value=" + expireTime);
            otherMap.put(key, expireTime);
        });

        // 删除
        idem.registerClearExpireHook(() -> {
            show("三方清理过期数据");
            for (Entry<String, Long> entry : otherMap.entrySet()) {
                if (System.currentTimeMillis() > entry.getValue()) {
                    otherMap.remove(entry.getKey());
                }
            }
            show("三方数据size：" + otherMap.size());
        });

        // 查询
        idem.registerSelectHook(key -> {
            show("三方查询数据：key=" + key);
            return otherMap.get(key);
        });

        // 是否为空
        idem.registerIsEmptyHook(() -> {
            show("三方是否为空数据");
            return otherMap.isEmpty();
        });

        int i = 0;

        while (i < 100) {
            if (i >= 20 && i <= 40) {
                show("空跑");
            } else {
                idem.contain("dada" + i);
            }
            sleep(1);
            i++;
        }
    }
}
