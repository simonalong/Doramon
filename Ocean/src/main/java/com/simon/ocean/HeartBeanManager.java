package com.simon.ocean;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 心跳管理器
 * <p>
 *     该类通常与类{@link SwitchBarrier}一起使用，心跳管理器管理业务的运行状态，而类{@link SwitchBarrier}管理对远端业务的访问
 * </p>
 * @author shizi
 * @since 2020-11-24 15:16:35
 */
@Slf4j
@UtilityClass
public class HeartBeanManager {

    /**
     * 心跳守护线程池
     */
    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), r -> {
        Thread thread = new Thread(r, "heart-daemon");
        thread.setDaemon(true);
        return thread;
    });
    /**
     * 心跳异常时候的阈值打印
     */
    private final Integer HEARD_BEAN_PRINT_THRESHOLD = 6;
    /**
     * 打印日志频率阈值
     */
    private Integer printLogThresholdNum = 0;
    /**
     * 业务的心跳任务
     */
    private final Map<String, Pair<String, Boolean>> heartTaskMap = new ConcurrentHashMap<>();

    static {
        scheduler.scheduleWithFixedDelay(HeartBeanManager::heartBeat, 5, 5, TimeUnit.SECONDS);
    }

    private static void heartBeat() {
        heartTaskMap.forEach((k, v) -> {
            try {
                HttpHelper.get(v.getKey());
                serverRestore(k, v.getValue());
                v.setValue(true);
            } catch (Throwable e) {
                serverUnAvailable(k);
                v.setValue(false);
            }
        });
    }

    /**
     * 添加业务的心跳判断
     *
     * @param bizName         业务名
     * @param remoteHealthUrl 业务的心跳检测url
     */
    public void addHeartWatch(String bizName, String remoteHealthUrl) {
        heartTaskMap.putIfAbsent(bizName, new Pair<>(remoteHealthUrl, true));
    }

    /**
     * 手动运行
     * @param bizName 业务名
     */
    public void handleRun(String bizName) {
        if (!heartTaskMap.containsKey(bizName)) {
            return;
        }
        Pair<String, Boolean> healthPair = heartTaskMap.get(bizName);
        try {
            HttpHelper.get(healthPair.getKey());
            serverRestore(bizName, healthPair.getValue());
            healthPair.setValue(true);
        } catch (Throwable e) {
            serverUnAvailable(bizName);
            healthPair.setValue(false);
        }
    }

    /**
     * 业务是否可用
     *
     * @param bizName 业务名
     * @return true：业务可用，false：业务不可用
     */
    public boolean isHealth(String bizName) {
        return SwitchBarrier.canCross(bizName);
    }

    private void serverRestore(String bizName, Boolean serverAvailable) {
        if (!serverAvailable) {
            log.info("服务【{}】心跳恢复", bizName);
        }
        printLogThresholdNum = 0;
        SwitchBarrier.allowCross(bizName);
    }

    private void serverUnAvailable(String bizName) {
        if (printLogThresholdNum <= 0) {
            log.error("服务【{}】心跳异常，url:{}", bizName, heartTaskMap.get(bizName).getKey());
            printLogThresholdNum = HEARD_BEAN_PRINT_THRESHOLD;
        } else {
            printLogThresholdNum--;
        }
        SwitchBarrier.forbidCross(bizName);
    }
}
