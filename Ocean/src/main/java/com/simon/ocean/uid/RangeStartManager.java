package com.simon.ocean.uid;

import com.simon.neo.Neo;
import lombok.extern.slf4j.Slf4j;

/**
 * 缓存起点的管理器
 *
 * @author zhouzhenyong
 * @since 2019/5/2 上午9:59
 */
@Slf4j
public final class RangeStartManager {

    private Neo neo;
    /**
     * 步长设置
     */
    private Integer stepSize;
    /**
     * 刷新第二buf的尺寸大小
     */
    private Integer refreshBufSize;
    /**
     * 缓存1的全局起点
     */
    private volatile Long rangeStartOfBuf1;
    /**
     * 缓存2的全局起点
     */
    private volatile Long rangeStartOfBuf2;
    /**
     * 当前使用的全局起点
     */
    private Long currentStart;

    /**
     * 是否刷新二级buf
     *
     * 每次buf切换，则将已刷新设置为未刷新
     */
    private volatile Boolean haveRefreshed = false;

    public RangeStartManager(Neo neo, Integer stepSize, Integer refreshBufSize){
        this.neo = neo;
        this.stepSize = stepSize;
        this.refreshBufSize = refreshBufSize;
    }

    /**
     * 初始化buf起点
     * @param rangeStart buff的其实位置
     * @return 当前位置
     */
    public Long initBufStart(Long rangeStart){
        rangeStartOfBuf1 = rangeStart;
        currentStart = rangeStartOfBuf1;
        return currentStart;
    }

    /**
     * 准备和刷新buf，如果满足刷新条件，则进行刷新二级buf
     * @param uid 全局id
     * @return true:达到刷新条件，且未刷新，false:没有到达刷新条件，或者到达刷新条件，但是已经刷新
     */
    public Boolean readyRefresh(Long uid) {
        if (uid - currentStart < refreshBufSize || haveRefreshed) {
            return false;
        }
        log.debug("到达刷新二级buf");
        return true;
    }

    /**
     * 获取新的buf范围
     *
     * 将其中不是当前的另外一个buf设置为新的buf
     *
     * @param rangeStart buf起始位置
     */
    public void refreshRangeStart(Long rangeStart){
        haveRefreshed = true;
        if(currentStart.equals(rangeStartOfBuf1)){
            rangeStartOfBuf2 = rangeStart;
            return;
        }

        if(currentStart.equals(rangeStartOfBuf2)){
            rangeStartOfBuf1 = rangeStart;
        }
    }

    /**
     * 到达其中一个buf的末尾部分
     *
     * @param uid 全局id
     * @return 0:没有到达末尾，1：刚好到达末尾，2：超过末尾
     */
    public Integer reachBufEnd(Long uid) {
        if (uid - currentStart + 1 == stepSize) {
            return 1;
        }

        if(uid - currentStart + 1 > stepSize){
            return 2;
        }
        return 0;
    }

    /**
     * buf切换起点
     *
     * 将current切换为另外的一个buf起点
     * @return 新的buf起点
     */
    public Long chgBufStart(){
        haveRefreshed = false;
        if (currentStart.equals(rangeStartOfBuf1)){
            currentStart = rangeStartOfBuf2;
            log.debug("buf起点切换，currentStart = " + currentStart);
            return currentStart;
        }

        if(currentStart.equals(rangeStartOfBuf2)){
            currentStart = rangeStartOfBuf1;
            log.debug("buf起点切换，currentStart = " + currentStart);
            return currentStart;
        }
        return null;
    }
}
