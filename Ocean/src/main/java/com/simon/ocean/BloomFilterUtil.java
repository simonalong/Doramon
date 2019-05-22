package com.simon.ocean;

import lombok.experimental.UtilityClass;

/**
 * 布隆过滤器的工具类，主要用于计算数组的大小，和哈希函数的个数
 * @author zhouzhenyong
 * @since 2018/11/3 上午10:46
 */
@UtilityClass
public class BloomFilterUtil {

    /**
     * m = - ((n * ln(p))/(ln2 * ln2))
     *
     * @param n 实际数据大小
     * @param p 误判率
     * @return long 获取位数组的大小
     */
    public long getBitsSize(long n, double p) {
        double r = -((n * Math.log(p)) / (Math.pow(Math.log(2), 2)));
        return Double.valueOf(r).longValue();
    }
    /**
     * k = (m/n) * ln2 约等于 0.7 * (m/n)，平常可自己这样估算
     *
     * @param m bitSize 位数组的bit个数
     * @param n 实际数据大小
     * @return long 获取哈希函数的最优个数
     */
    public long getHashNum(long m, long n) {
        double r = (m / n) * Math.log(2);
        return Double.valueOf(r).longValue();
    }


    public long getBitsSizeOnByte(long n, double p) {
        return getBitsSize(n, p) / 8;
    }

    public long getBitsSizeOnKByte(long n, double p) {
        return getBitsSizeOnByte(n, p) / 1024;
    }

    public long getBitsSizeOnMByte(long n, double p) {
        return getBitsSizeOnKByte(n, p) / 1024;
    }

    public long getBitsSizeOnGByte(long n, double p) {
        return getBitsSizeOnMByte(n, p) / 1024;
    }
}
