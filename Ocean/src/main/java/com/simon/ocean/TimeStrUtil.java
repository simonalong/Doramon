package com.simon.ocean;

import java.util.Date;
import lombok.experimental.UtilityClass;

/**
 * 主要用于计算两个时间之间的差值，毫秒，秒，分钟，小时，天，周之间的转换，主要是字符的展示
 *
 * @author zhouzhenyong
 * @since 2019/2/27 下午4:12
 */
@UtilityClass
public class TimeStrUtil {

    /**
     * 我们这里最小的单位是毫秒
     */
    private static final long MILLIS = 1;
    /**
     * 秒
     */
    private static final long SECOND = 1000 * MILLIS;
    /**
     * 分钟
     */
    private static final long MINUTE = 60 * SECOND;
    /**
     * 小时
     */
    private static final long HOUR = 60 * MINUTE;
    /**
     * 天
     */
    private static final long DAY = 24 * HOUR;
    /**
     * 周
     */
    private static final long WEEK = 7 * DAY;

    /**
     * 计算两个时间的差值，用字符表示
     *
     * @return 返回两个值的差值的字符展示：举例：4天 1分钟 12秒 132毫秒
     */
    public String parseDuration(Date date1, Date date2) {
        if (date1.getTime() > date2.getTime()) {
            return parseTime(date1.getTime() - date2.getTime());
        } else {
            return parseTime(date2.getTime() - date1.getTime());
        }
    }

    /**
     * 计算两个时间的差值，用字符表示
     *
     * @return 返回两个值的差值的字符展示：举例：1周 1分钟 12秒 132毫秒
     */
    public String parseDurationWeek(Date date1, Date date2) {
        if (date1.getTime() > date2.getTime()) {
            return parseWeeks(date1.getTime() - date2.getTime());
        } else {
            return parseWeeks(date2.getTime() - date1.getTime());
        }
    }

    /**
     * 根据传入的参数自动返回对应的字符：毫秒，秒，分钟，小时，天：其中这里默认按照天来计算
     * @return 返回时间的字符展示：举例：2天 1分钟 12秒
     */
    public String parseTime(long time) {
        return parseDays(time);
    }

    /**
     * 周：按周来显示
     * 举例：1周 1分钟 12秒 132毫秒
     */
    public String parseWeeks(long time) {
        StringBuilder sb = new StringBuilder();
        if (canWeek(time)) {
            sb.append(time / WEEK).append("周 ").append(parseDays(time % WEEK));
        } else {
            sb.append(parseDays(time % WEEK));
        }
        return sb.toString();
    }

    /**
     * 天
     */
    private String parseDays(long time) {
        StringBuilder sb = new StringBuilder();
        if (canDay(time)) {
            sb.append(time / DAY).append("天 ").append(parseHours(time % DAY));
        } else {
            sb.append(parseHours(time % DAY));
        }
        return sb.toString();
    }

    /**
     * 小时
     */
    private String parseHours(long time) {
        StringBuilder sb = new StringBuilder();
        if (canHour(time)) {
            sb.append(time / HOUR).append("小时 ").append(parseMinutes(time % HOUR));
        } else {
            sb.append(parseMinutes(time % HOUR));
        }
        return sb.toString();
    }

    /**
     * 分钟
     */
    private String parseMinutes(long time) {
        StringBuilder sb = new StringBuilder();
        if (canMinute(time)) {
            sb.append(time / MINUTE).append("分钟 ").append(parseSeconds(time % MINUTE));
        } else {
            sb.append(parseSeconds(time % MINUTE));
        }
        return sb.toString();
    }

    /**
     * 秒
     */
    private String parseSeconds(long time) {
        StringBuilder sb = new StringBuilder();
        if (canSecond(time)) {
            sb.append(time / SECOND).append("秒 ").append(parseMillis(time % SECOND));
        } else {
            sb.append(parseMillis(time % SECOND));
        }
        return sb.toString();
    }

    /**
     * 毫秒
     */
    private String parseMillis(long time) {
        StringBuilder sb = new StringBuilder();
        if (canMillis(time)) {
            sb.append(time).append("毫秒");
        }
        return sb.toString();
    }

    private boolean canWeek(long time) {
        return time > WEEK;
    }

    private boolean canDay(long time) {
        return time > DAY;
    }

    private boolean canHour(long time) {
        return time > HOUR;
    }

    private boolean canMinute(long time) {
        return time > MINUTE;
    }

    private boolean canSecond(long time) {
        return time > SECOND;
    }

    private boolean canMillis(long time) {
        return time > MILLIS;
    }
}