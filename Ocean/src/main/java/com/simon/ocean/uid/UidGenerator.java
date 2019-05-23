package com.simon.ocean.uid;

import com.simon.neo.Neo;
import com.simon.neo.NeoMap;
import com.simon.neo.exception.RefreshRatioException;
import com.simon.neo.uid.RangeStartManager;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 分布式全局id生成器
 * @author zhouzhenyong
 * @since 2019/5/1 下午10:22
 */
public final class UidGenerator {

    private Neo neo;
    /**
     * 步长，默认1万
     */
    private Integer stepSize = 10 * 1000;
    /**
     * 全局id生成器的表名
     */
    private static final String UUID_TABLE = "neo_id_generator";
    /**
     * 全局id生成器表的唯一id
     */
    private static final Integer TABLE_ID = 1;
    /**
     * id生成表是否已经初始化
     */
    private volatile Boolean tableInitFlag = false;
    /**
     * 全局id
     */
    private volatile AtomicLong uuidIndex = new AtomicLong();
    /**
     * 范围管理器
     */
    private RangeStartManager rangeManager;

    private static volatile UidGenerator instance;

    private UidGenerator(){}

    /**
     * 全局id生成器的单例
     *
     * @param neo 数据库对象
     * @param stepSize 步长
     * @param refreshRatio 刷新第二缓存的比率，用于在到达一定长度时候设置新的全局id起点，范围：0~1.0
     * @return 全局id生成器对象
     */
    public static UidGenerator getInstance(Neo neo, Integer stepSize, Float refreshRatio){
        if (refreshRatio < 0.0 || refreshRatio > 1.0) {
            throw new RefreshRatioException("参数：refreshRation不合法，为" + refreshRatio);
        }
        if (null == instance) {
            synchronized (UidGenerator.class) {
                if (null == instance) {
                    instance = new UidGenerator();
                    instance.neo = neo;
                    instance.stepSize = stepSize;
                    instance.init(neo, stepSize, refreshRatio);
                }
            }
        }
        return instance;
    }

    /**
     * 全局id生成器的单例，采用默认步长和刷新比例
     * @param neo 数据库对象
     * @return 全局id生成器对象
     */
    public static UidGenerator getInstance(Neo neo){
        if (null == instance) {
            synchronized (UidGenerator.class) {
                if (null == instance) {
                    instance = new UidGenerator();
                    instance.neo = neo;
                    instance.init(neo, instance.stepSize, 0.2f);
                }
            }
        }
        return instance;
    }

    public Long getUid(){
        Long uid = uuidIndex.getAndIncrement();
        // 到达刷新buf的位置则进行刷新二级缓存
        if(rangeManager.readyRefresh(uid)){
            synchronized (UidGenerator.class){
                if(rangeManager.readyRefresh(uid)){
                    rangeManager.refreshRangeStart(allocStart());
                }
            }
        }

        // 刚好到达末尾，则切换起点，对于没有来得及切换，增长超过范围的，则重新分配
        Integer reachResult = rangeManager.reachBufEnd(uid);
        if (1 == reachResult) {
            uuidIndex.set(rangeManager.chgBufStart());
            return uid;
        } else if (2 == reachResult) {
            return uuidIndex.getAndIncrement();
        }
        return uid;
    }

    private void init(Neo neo, Integer stepSize, Float refreshRatio) {
        rangeManager = new RangeStartManager(neo, stepSize, getRefreshBufSize(stepSize, refreshRatio));
        tableInitPreHandle();
        this.uuidIndex.set(rangeManager.initBufStart(allocStart()));
    }

    /**
     * 设置刷新尺寸
     * @param stepSize 步长
     * @param refreshRatio float类型的刷新比率
     */
    private Integer getRefreshBufSize(Integer stepSize, Float refreshRatio) {
        return (int) (stepSize * refreshRatio);
    }

    /**
     * 数据库分配新的范围起点
     *
     * @return 返回数据库最新分配的值
     */
    private Long allocStart() {
        return neo.tx(() -> {
            Long value = neo.value(Long.class, UUID_TABLE, "uuid", NeoMap.of("id", TABLE_ID));
            neo.execute("update %s set `uuid` = `uuid` + ? where `id` = ?", UUID_TABLE, stepSize, TABLE_ID);
            return value;
        });
    }

    /**
     * 用于全局表的初始化，若全局表没有创建，则创建
     */
    private void tableInitPreHandle() {
        if (!tableInitFlag) {
            synchronized (UidGenerator.class) {
                if (!tableInitFlag) {
                    initTable();
                    tableInitFlag = true;
                }
            }
        }
    }

    private void initTable() {
        if (!neo.tableExist(UUID_TABLE)) {
            neo.execute(uidTableCreateSql());
            neo.initDb();
            neo.insert(UUID_TABLE, NeoMap.of("id", TABLE_ID, "uuid", 1));
        }
    }

    private String uidTableCreateSql() {
        return "create table `" + UUID_TABLE + "` (\n"
            + "  `id` int(11) not null,\n"
            + "  `uuid` bigint(20) not null default 0,\n"
            + "  primary key (`id`)\n"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }
}
