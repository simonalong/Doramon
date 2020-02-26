package com.simon.ocean;

import static javax.management.timer.Timer.*;

import java.util.Date;
import javax.management.timer.Timer;
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
            sb.append(time / Timer.ONE_WEEK).append("周 ").append(parseDays(time % Timer.ONE_WEEK));
        } else {
            sb.append(parseDays(time % Timer.ONE_WEEK));
        }
        return sb.toString();
    }

    /**
     * 天
     */
    private String parseDays(long time) {
        StringBuilder sb = new StringBuilder();
        if (canDay(time)) {
            sb.append(time / ONE_DAY).append("天 ").append(parseHours(time % ONE_DAY));
        } else {
            sb.append(parseHours(time % ONE_DAY));
        }
        return sb.toString();
    }

    /**
     * 小时
     */
    private String parseHours(long time) {
        StringBuilder sb = new StringBuilder();
        if (canHour(time)) {
            sb.append(time / ONE_HOUR).append("小时 ").append(parseMinutes(time % ONE_HOUR));
        } else {
            sb.append(parseMinutes(time % ONE_HOUR));
        }
        return sb.toString();
    }

    /**
     * 分钟
     */
    private String parseMinutes(long time) {
        StringBuilder sb = new StringBuilder();
        if (canMinute(time)) {
            sb.append(time / ONE_MINUTE).append("分钟 ").append(parseSeconds(time % ONE_MINUTE));
        } else {
            sb.append(parseSeconds(time % ONE_MINUTE));
        }
        return sb.toString();
    }

    /**
     * 秒
     */
    private String parseSeconds(long time) {
        StringBuilder sb = new StringBuilder();
        if (canSecond(time)) {
            sb.append(time / ONE_SECOND).append("秒 ").append(parseMillis(time % ONE_SECOND));
        } else {
            sb.append(parseMillis(time % ONE_SECOND));
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
        return time > ONE_WEEK;
    }

    private boolean canDay(long time) {
        return time > ONE_DAY;
    }

    private boolean canHour(long time) {
        return time > ONE_HOUR;
    }

    private boolean canMinute(long time) {
        return time > ONE_MINUTE;
    }

    private boolean canSecond(long time) {
        return time > ONE_SECOND;
    }

    private boolean canMillis(long time) {
        return time > 1;
    }
}