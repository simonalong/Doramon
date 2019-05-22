package com.simon.ocean

import org.junit.Assert
import spock.lang.Specification

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

/**
 * @author zhouzhenyong
 * @since 2019/2/27 下午4:55
 */
class TimeStrUtilDemo extends Specification {

    def "测试1"() {
        expect:
        Assert.assertEquals(result, TimeStrUtil.parseTime(time))

        where:
        time                                                   || result
        TimeUnit.MILLISECONDS.toMillis(1)                      || "1毫秒"
        TimeUnit.MILLISECONDS.toMillis(2)                      || "2毫秒"
        TimeUnit.MILLISECONDS.toMillis(3)                      || "3毫秒"
        TimeUnit.MILLISECONDS.toMillis(7)                      || "7毫秒"
        TimeUnit.SECONDS.toMillis(7)                           || "7秒 "
        TimeUnit.HOURS.toMillis(7)                             || "7小时 "
        TimeUnit.DAYS.toMillis(7) + TimeUnit.HOURS.toMillis(2) || "7天 2小时 "
    }

    def "测试2"() {
        expect:
        Assert.assertEquals(result, TimeStrUtil.parseWeeks(time))

        where:
        time                                                      || result
        TimeUnit.MILLISECONDS.toMillis(7)                         || "7毫秒"
        TimeUnit.SECONDS.toMillis(7)                              || "7秒 "
        TimeUnit.DAYS.toMillis(7) + TimeUnit.HOURS.toMillis(2)    || "1周 2小时 "
        TimeUnit.DAYS.toMillis(12) + TimeUnit.MINUTES.toMillis(2) || "1周 5天 2分钟 "
    }

    def "测试3"() {
        expect:
        Assert.assertEquals(result, TimeStrUtil.parseTime(end - start))

        where:
        end                                                    | start                                                  || result
        TimeUnit.MILLISECONDS.toMillis(7)                      | TimeUnit.MILLISECONDS.toMillis(3)                      || "4毫秒"
        TimeUnit.SECONDS.toMillis(7)                           | TimeUnit.SECONDS.toMillis(3)                           || "4秒 "
        TimeUnit.HOURS.toMillis(7)                             | TimeUnit.HOURS.toMillis(3)                             || "4小时 "
        TimeUnit.DAYS.toMillis(7) + TimeUnit.HOURS.toMillis(2) | TimeUnit.DAYS.toMillis(3) + TimeUnit.HOURS.toMillis(4) || "3天 22小时 "
    }

    def "测试4"() {
        expect:

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date1 = format.parse("2010-12-20 12:02:12.321")
        Date date2 = format.parse("2010-12-30 10:00:10.301")

        // 9天 21小时 57分钟 57秒 980毫秒
        println TimeStrUtil.parseDuration(date1, date2)
        // 1周 2天 21小时 57分钟 57秒 980毫秒
        println TimeStrUtil.parseDurationWeek(date1, date2)
    }
}
