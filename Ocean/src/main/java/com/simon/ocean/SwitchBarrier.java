package com.simon.ocean;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 开关栅栏，名字跟{@link java.util.concurrent.CyclicBarrier} 有点相似，也是控制多个线程的，但是概念上不同，{@link com.simon.ocean.SwitchBarrier} 是通过api的方式在代码层面上对任意个数线程进行控制，
 * 而{@link java.util.concurrent.CyclicBarrier}中线程个数是指定的，而且阻塞是自动释放。在一些场景中无法使用，我们这里借鉴栅栏（Barrier）的概念，通过开和关的方式对不定个数的多线程进行群控
 * {@code
 * SwitchBarrier switchBarrier = new SwitchBarrier();
 * switchBarrier.forbidCross();
 * ...
 * // 如下执行到如下的代码的线程会阻塞掉，只有其他位置执行{@code switchBarrier.allowCross()}后，阻塞在这里的线程才会再次执行
 * switchBarrier.cross();
 * }
 * <br/>
 * 这里也额外提供了一个静态内置map，也可以通过静态方式的方式对某个"开关栅栏"进行开启和关闭
 * {@code
 * // 一旦执行这里，则经过SwitchBarrier.cross("xxx");的线程会被阻塞
 * SwitchBarrier.forbidCross("xxx");
 * ...
 * // 如果'栅栏'关闭，则会阻塞在这里，等待allowCross("xxx")执行后才能再次执行
 * SwitchBarrier.cross("xxx");
 * ...
 * // 执行这里，则阻塞在 SwitchBarrier.cross("xxx"); 这里的线程会重新被唤醒
 * SwitchBarrier.allowCross("xxx");
 * }
 * <p>
 * 提示：
 * 开启多次和开启一次效果是一样的，同样，关闭多次和关闭一次效果也是一样的，穿过一次和穿过多次效果也是一样（前提是开关没有变化）
 *
 * @author shizi
 * @since 2020-11-23 12:01:36
 */
public class SwitchBarrier {

    private final Sync sync;
    /**
     * 开关栅栏名称map
     */
    private static final Map<String, SwitchBarrier> SWITCH_BARRIER_MAP = new ConcurrentHashMap<>();

    /**
     * state为0认为栅栏放下（关闭），不允许通行，为1或其他则表示栅栏拉起（开启），允许通行
     */
    private static final class Sync extends AbstractQueuedSynchronizer implements Serializable {

        Sync() {
            // 可以通行
            allowCross();
        }

        boolean canCross() {
            return 0 != getState();
        }

        void allowCross() {
            setState(1);
        }

        void forbidCross() {
            setState(0);
        }

        /**
         * 这里只要返回小于0 的，在share阻塞模式中就会进行阻塞
         *
         * @param acquires 忽略
         * @return {@code -1} 在state为0时候进行阻塞使用
         */
        @Override
        protected int tryAcquireShared(int acquires) {
            return 0 == getState() ? -1 : 1;
        }

        /**
         * 释放
         *
         * @param releases release个数
         * @return {@code true}
         */
        @Override
        protected boolean tryReleaseShared(int releases) {
            return true;
        }
    }

    public SwitchBarrier() {
        sync = new Sync();
    }

    /**
     * 构造函数
     * @param allow 默认配置，是否允许通过
     */
    public SwitchBarrier(Boolean allow) {
        sync = new Sync();
        if (!allow) {
            sync.forbidCross();
        }
    }

    /**
     * 开启栅栏，禁止通过
     * <p>
     * 该函数执行后，执行函数{@link SwitchBarrier#cross()}的线程就会阻塞在这里
     */
    public void forbidCross() {
        sync.forbidCross();
    }

    /**
     * 开启某个栅栏
     * <p>
     * 该函数执行后，执行函数{@link SwitchBarrier#cross(String)}的线程就会阻塞在这里
     */
    public static void forbidCross(String name) {
        SwitchBarrier switchBarrierCache = SWITCH_BARRIER_MAP.get(name);
        if (null != switchBarrierCache) {
            switchBarrierCache.forbidCross();
        } else {
            SwitchBarrier switchBarrier = new SwitchBarrier();
            switchBarrier.forbidCross();
            SWITCH_BARRIER_MAP.put(name, switchBarrier);
        }
    }

    /**
     * 关闭栅栏，允许通过
     * <p>
     * 该函数执行后，会通知阻阻塞在函数{@link SwitchBarrier#cross()}的线程重新执行，同时后续经过函数{@link SwitchBarrier#cross()}会直接通过
     */
    public void allowCross() {
        sync.allowCross();
        sync.releaseShared(1);
    }

    /**
     * 关闭某个栅栏
     * <p>
     * 该函数执行后，会通知阻塞在函数{@link SwitchBarrier#cross(String)}这里的多个线程重新执行，同时后续经过函数{@link SwitchBarrier#cross(String)}会直接通过
     */
    public static void allowCross(String name) {
        SwitchBarrier switchBarrierCache = SWITCH_BARRIER_MAP.get(name);
        if (null != switchBarrierCache) {
            switchBarrierCache.allowCross();
        } else {
            SwitchBarrier switchBarrier = new SwitchBarrier();
            switchBarrier.allowCross();
            SWITCH_BARRIER_MAP.put(name, switchBarrier);
        }
    }

    /**
     * 通过栅栏
     * <p>
     * 如果开启了栅栏，则经过该函数的线程阻塞，否则直接通过
     */
    public void cross() throws InterruptedException {
        sync.acquireSharedInterruptibly(0);
    }

    public static void cross(String name) throws InterruptedException {
        SwitchBarrier switchBarrierCache = SWITCH_BARRIER_MAP.get(name);
        if (null != switchBarrierCache) {
            switchBarrierCache.cross();
        } else {
            SwitchBarrier switchBarrier = new SwitchBarrier();
            switchBarrier.cross();
            SWITCH_BARRIER_MAP.put(name, switchBarrier);
        }
    }

    /**
     * 栅栏是否可以通过
     */
    public boolean canCross() {
        return sync.canCross();
    }

    /**
     * 某个栅栏是否可以通过
     */
    public static boolean canCross(String name) {
        SwitchBarrier switchBarrierCache = SWITCH_BARRIER_MAP.get(name);
        if (null != switchBarrierCache) {
            return switchBarrierCache.canCross();
        } else {
            SwitchBarrier switchBarrier = new SwitchBarrier();
            SWITCH_BARRIER_MAP.put(name, switchBarrier);
            return switchBarrier.canCross();
        }
    }

    public static void put(String name, SwitchBarrier switchBarrier) {
        SWITCH_BARRIER_MAP.put(name, switchBarrier);
    }

    public static SwitchBarrier getSwitchBarrier(String name) {
        return SWITCH_BARRIER_MAP.get(name);
    }
}
