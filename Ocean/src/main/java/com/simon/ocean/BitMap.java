package com.simon.ocean;

/**
 * 该工具类算是布隆过滤器在数据为int时候的简化和优化版，内存可以更小
 * 功能：
 *  1.添加数据
 *  2.清理数据
 *  3.判断存在否
 *  4.插入的数据的个数
 * @author zhouzhenyong
 * @since 2018/11/3 下午7:02
 */
public class BitMap {

    private final static int SHIFT = 5;
    /**
     * int占用bit大小，32
     */
    private final static int BITWORD = 1 << SHIFT;
    /**
     * 掩码
     */
    private final static int MASK = BITWORD - 1;
    /**
     * 该bitMap可以存储的最大的数据，为2^32次方，如果采用int作为数组，则为2^27个int即128 * 2^20
     */
    private final static int M = 1024 * 1024;
    /**
     * flag分配的个数
     */
    private int dataSize;
    /**
     * 数据标记数组
     */
    private int[] flags;

    private static volatile BitMap instance;
    private BitMap(){}

    public static BitMap getInstance(){
        if(null == instance){
            synchronized (BitMap.class){
                if(null == instance){
                    instance = new BitMap();
                    instance.initMaxValue();
                }
            }
        }
       return instance;
    }

    /**
     * 设置数据的可以存储的最大的值
     * 注意：该工具没有做旧数据到新数据的迁移，一旦执行则旧的数据会被清理掉
     * @param size 可以校验的最大的数据
     */
    public void setMaxValue(int size){
        if ((size & BITWORD - 1) == 0){
            dataSize = size / BITWORD;
            flags = new int[dataSize];
        }else{
            dataSize = size / BITWORD + 1;
            flags = new int[dataSize];
        }
    }

    /**
     * 保存数据
     */
    public void insert(int data) {
        int index = data >> SHIFT;
        if (index >= dataSize){
            throw new SizeOutOfBoundsException();
        }
        flags[index] |= 1 << (data & MASK);
    }

    /**
     * 判断是否包含对应的数据
     * @return 包含返回true，否则false
     */
    public boolean contain(int data){
        return getIndex(data) != 0;
    }

    /**
     * 清理数据
     */
    public void delete(int data) {
        int index = data >> SHIFT;
        if (index >= dataSize){
            throw new SizeOutOfBoundsException();
        }
        flags[index] &= ~(1 << (data & MASK));
    }

    /**
     * 返回bitmap中元素的个数
     *
     * @return bitmap中元素的个数
     */
    public int count() {
        int cnt = 0;
        for (int a : flags) {
            cnt += count(a);
        }
        return cnt;
    }

    /**
     * 设置数据的可以存储的默认的最大值，为2^32次方，如果采用int作为数组，则除以32，则为2^27个int，即128 * 2^20，占内存512MB数据
     */
    private void initMaxValue(){
        dataSize = 128 * M;
        flags = new int[dataSize];
    }

    /**
     * 返回某个数据的状态
     *
     * @return 0或2^k,k表示在BitWord单元中的位置
     */
    private int getIndex(int data) {
        int index = data >> SHIFT;
        if (index >= dataSize){
            return -1;
        }
        return flags[data >> SHIFT] & (1 << (data & MASK));
    }

    /**
     * 统计二进制几个1
     *
     * @param value 待统计的整数
     */
    private int count(int value) {
        int one = 0;
        while (value != 0) {
            one++;
            value &= value - 1;
        }
        return one;
    }

    public class SizeOutOfBoundsException extends ArrayIndexOutOfBoundsException{
        SizeOutOfBoundsException(){
            super("数据超过最大值");
        }
    }
}
