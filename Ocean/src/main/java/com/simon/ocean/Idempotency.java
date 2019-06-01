package com.simon.ocean;

import com.alibaba.fastjson.JSON;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 高性能单机幂等性判断工具
 * 功能
 * 1.数据判断是否存在或者失效：如果不存在或失效，则自动添加到内部缓存中
 * 2.数据过期失效被清理：在过期后会自动清理对应的缓存数据，不会造成数据浪费
 *
 * 高性能：单机化弱依赖DB，数据经过非逆向压缩进行内存存储，最大可存储100万条数据（内存大概占用222.2M），最大条数也可设置
 * 可扩展：在数据超过100万条的时候，更多的数据放第三方（DB或者Redis）存储结构中
 *
 * @author zhouzhenyong
 * @since 2019/2/21 下午7:36
 */
public class Idempotency {

    private static Idempotency instance = new Idempotency();
    private static final String LOG_PRE = "[Idempotency]";
    /**
     * 幂等性的数据缓存表，这里采用线程安全的有序的Map，通过expireTime和dataStr合并进行排序，从而得到按照时间排序的，删除和插入都比较方便
     */
    private ConcurrentSkipListMap<String, Long> dataMap = new ConcurrentSkipListMap<>();
    /**
     * 过期时间单位设置：默认设置为20秒，向后延长20的mills
     */
    private long backExpireTimeMills = TimeUnit.SECONDS.toMillis(20);
    /**
     * 我们这里设定最大内存的数据存储量，如果超过这个数值100万条数据（经过计算大概：222.2MB），则将新的数据插入到外部第三方（DB或者Redis）存储中，以保证内存的正常
     */
    private Integer maxDataSize = 100 * 10000;
    /**
     * 外部数据标志，表示外部存储是否还有可用数据，用于清理和查找时候判断
     */
    private volatile Boolean outFlag = false;
    /**
     * 过期数据清理守护线程
     */
    private ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1, r -> {
        Thread thread = new Thread(r, "Thread-Idempotency-deamon");
        thread.setDaemon(true);
        return thread;
    });

    /**
     * 第三方处理回调：数据插入（用于将对象和对象的过期时间保存）
     * param1：为对象的唯一key
     * param2：为对象的过期时间
     */
    private BiConsumer<String, Long> insertHook;
    /**
     * 第三方处理回调：数据删除
     * 逻辑为：删除已经过期的对象（即：当前时间的毫秒数大于数据的过期时间）
     */
    private Runnable clearExpireHook;
    /**
     * 第三方处理回调：数据获取
     * param1：为对象的唯一key
     * param2：对象的过期时间
     */
    private Function<String, Long> selectHook;
    /**
     * 第三方处理回调：是否为空，第三方数据池中的数据是否为空，用于后面删除时候判断是否需要去第三方数据池删除，如果已经为空则不需要调用第三方了
     * return：是否为空
     */
    private Supplier<Boolean> isEmptyHook;
    /**
     * 第三方回调执行器
     */
    private Executor hookExecutor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.MINUTES,
        new ArrayBlockingQueue<>(20), r-> new Thread(r, "Thread-Idempotency-hook"), new DiscardPolicy());

    private Idempotency() {
        initDeamon();
    }

    public static Idempotency getInstance() {
        return instance;
    }

    private void initDeamon(){
        scheduler.scheduleWithFixedDelay(()->{
            for (Entry<String, Long> entry : dataMap.entrySet()) {
                if(System.currentTimeMillis() > entry.getValue()){
                    dataMap.remove(entry.getKey());
                }
            }

            // 处理第三方清理逻辑
            deleteOther();
        }, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * 清理第三方的数据缓冲池
     */
    private void deleteOther(){
        if (outFlag){
            if(null != isEmptyHook){
                // 如果第三方为空了，则关闭第三方标志，否则继续调用清理逻辑
                if (isEmptyHook.get()){
                    outFlag = false;
                } else {
                    if(null != isEmptyHook && !isEmptyHook.get()){
                        if (null != clearExpireHook){
                            hookExecutor.execute(clearExpireHook);
                        }
                    }
                }
            }
        }
    }

    /**
     * 判断当前是否含有对应的数据，不包含则将对应的数据插入到缓存中
     */
    public boolean contain(Object... object) {
        // 如果为null，则不进行数据的校验
        if (null == object){
            return false;
        }
        String key = buildKey(object);
        if (innerContain(key)) {
            return true;
        }

        insert(key);
        return false;
    }

    /**
     * 设置数据的实效性，超过这个时间就会失效，失效之后，如果还有这样的消息过来，则认为新的消息是OK的
     *
     * @param num 数据时长
     */
    public Idempotency setExpire(Integer num, TimeUnit timeUnit) {
        this.backExpireTimeMills = timeUnit.toMillis(num);
        return this;
    }

    /**
     * 提供可修改的最大值，不过内存占用大小，需要自己计算
     */
    public Idempotency setMaxDataSize(Integer size){
        this.maxDataSize = size;
        return this;
    }

    /**
     * 注册第三方存储的数据插入回调
     */
    public Idempotency registerInsertHook(BiConsumer<String, Long> insertHook){
        this.outFlag = true;
        this.insertHook = insertHook;
        return this;
    }

    /**
     * 注册第三方存储的数据删除回调，
     */
    public Idempotency registerClearExpireHook(Runnable clearExpireHook){
        this.outFlag = true;
        this.clearExpireHook = clearExpireHook;
        return this;
    }

    /**
     * 注册第三方存储的数据选择回调
     */
    public Idempotency registerSelectHook(Function<String, Long> selectHook){
        this.outFlag = true;
        this.selectHook = selectHook;
        return this;
    }

    /**
     * 注册第三方存储的数据是否为空的回调
     */
    public Idempotency registerIsEmptyHook(Supplier<Boolean> isEmptyHook){
        this.outFlag = true;
        this.isEmptyHook = isEmptyHook;
        return this;
    }

    /**
     * 对于不包含的数据加入其中，对于已经包含的则刷新数据的过期时间
     */
    private void insert(String key) {
        if (dataMap.size() <= maxDataSize){
            dataMap.compute(key, (k, v) -> System.currentTimeMillis() + backExpireTimeMills);
            return;
        }

        // 如果设置了第三方配置，则对于超过阈值的数据则将数据存到第三方
        if (null != insertHook) {
            hookExecutor.execute(() -> insertHook.accept(key, System.currentTimeMillis() + backExpireTimeMills));
        }else{
            // 若没有设置第三方，则通过LRU覆盖对应的数据
        }
    }

    /**
     * 判断当前是否含有对应的数据，如果包含还要看下该配置是否过期，如果过期则返回false
     *
     * @param key 需要校验的数据的key
     * @return
     * true: 数据包含，而且数据在过期时间内还可用
     * false：数据不包含，或者数据包含，但是数据过期不可用
     */
    private boolean innerContain(String key) {
        if (dataMap.containsKey(key)) {
            return System.currentTimeMillis() <= dataMap.get(key);
        }

        // 如果开启第三方，且支持查询回调，则获取三方数据的过期时间，如果过期则返回
        if(outFlag){
            if (null != selectHook){
                Long expireTime3f = selectHook.apply(key);
                if (null != expireTime3f) {
                    return System.currentTimeMillis() <= expireTime3f;
                }
            }
        }
        return false;
    }

    /**
     * 数据唯一识别，这里采用将类的CanonicalName和对象的toString一起，然后再通过SHA256压缩以存储更多数据
     */
    private String buildKey(Object... object) {
        // 在循环中String禁止使用在内部+拼接
        StringBuilder sb = new StringBuilder();
        return Encrypt.encode(Stream.of(object)
            .map(o-> sb
                .append(o.getClass().getCanonicalName())
                .append(":")
                .append(JSON.toJSONString(o))
                .toString())
            .reduce((a,b)-> sb
                .append(a)
                .append("-")
                .append(b)
                .toString()).get());
    }

    /**
     * 用于将对象的字符压缩，放置数据的字符过长
     */
    private static class Encrypt {

        /**
         * 利用java原生的摘要实现SHA256加密
         *
         * @param str 加密后的报文
         */
        private static String encode(String str) {
            MessageDigest messageDigest;
            String encodeStr = "";
            try {
                messageDigest = MessageDigest.getInstance("SHA-256");
                messageDigest.update(str.getBytes("UTF-8"));

                // 将byte 转换为字符展示出来
                StringBuilder stringBuffer = new StringBuilder();
                String temp;
                for (byte aByte : messageDigest.digest()) {
                    temp = Integer.toHexString(aByte & 0xFF);
                    if (temp.length() == 1) {
                        //1得到一位的进行补0操作
                        stringBuffer.append("0");
                    }
                    stringBuffer.append(temp);
                }
                encodeStr = stringBuffer.toString();
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return encodeStr;
        }
    }
}
