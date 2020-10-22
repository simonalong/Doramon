package com.simon.ocean;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.simon.ocean.LocalDateTimeUtil.*;


/**
 * @author shizi
 * @since 2020/10/19 6:44 下午
 */
public class LocalDateTimeUtilTest {

    @Test
    public void localDateTimeToOtherTest() {
        LocalDateTime localDateTime = LocalDateTime.of(2020, 12, 20, 12, 12, 12, 123);

        Assert.assertEquals(Long.valueOf(1608437532000L), LocalDateTimeUtil.localDateTimeToLong(localDateTime));
        Assert.assertEquals("Sun Dec 20 12:12:12 CST 2020", LocalDateTimeUtil.localDateTimeToDate(localDateTime).toString());
        Assert.assertEquals("2020-12-20 12:12:12", LocalDateTimeUtil.localDateTimeToString(localDateTime));

        Assert.assertEquals("2020-12-20 12:12:12.000", LocalDateTimeUtil.localDateTimeToString(localDateTime, yMdHmsSSS));
        Assert.assertEquals("2020-12-20 12:12:12", LocalDateTimeUtil.localDateTimeToString(localDateTime, yMdHms));
        Assert.assertEquals("2020-12-20 12:12", LocalDateTimeUtil.localDateTimeToString(localDateTime, yMdHm));
        Assert.assertEquals("2020-12-20 12", LocalDateTimeUtil.localDateTimeToString(localDateTime, yMdH));
        Assert.assertEquals("2020-12-20", LocalDateTimeUtil.localDateTimeToString(localDateTime, yMd));
        Assert.assertEquals("2020-12", LocalDateTimeUtil.localDateTimeToString(localDateTime, yM));
        Assert.assertEquals("2020", LocalDateTimeUtil.localDateTimeToString(localDateTime, y));

        Assert.assertEquals("2020-12-20 12:12:12.000", LocalDateTimeUtil.localDateTimeToString(localDateTime, DateTimeFormatter.ofPattern(yMdHmsSSS)));
        Assert.assertEquals("2020-12-20 12:12:12", LocalDateTimeUtil.localDateTimeToString(localDateTime, DateTimeFormatter.ofPattern(yMdHms)));
        Assert.assertEquals("2020-12-20 12:12", LocalDateTimeUtil.localDateTimeToString(localDateTime, DateTimeFormatter.ofPattern(yMdHm)));
        Assert.assertEquals("2020-12-20 12", LocalDateTimeUtil.localDateTimeToString(localDateTime, DateTimeFormatter.ofPattern(yMdH)));
        Assert.assertEquals("2020-12-20", LocalDateTimeUtil.localDateTimeToString(localDateTime, DateTimeFormatter.ofPattern(yMd)));
        Assert.assertEquals("2020-12", LocalDateTimeUtil.localDateTimeToString(localDateTime, DateTimeFormatter.ofPattern(yM)));
        Assert.assertEquals("2020", LocalDateTimeUtil.localDateTimeToString(localDateTime, DateTimeFormatter.ofPattern(y)));
    }

    @Test
    public void localDateToOtherTest() {
        LocalDate localDate = LocalDate.of(2020, 12, 20);

        Date date = LocalDateTimeUtil.stringToDate("2020-12-20");
        Assert.assertEquals(date, LocalDateTimeUtil.localDateToDate(localDate));
        Assert.assertEquals(Long.valueOf(1608393600000L), LocalDateTimeUtil.localDateToLong(localDate));
        Assert.assertEquals("2020-12-20", LocalDateTimeUtil.localDateToString(localDate));

        LocalDateTime localDateTime = LocalDateTime.of(2020, 12, 20, 0, 0, 0);
        Assert.assertEquals(localDateTime, LocalDateTimeUtil.localDateToLocalDateTime(localDate));
    }

    @Test
    public void dateToOtherTest() {
        Date date = LocalDateTimeUtil.stringToDate("2020-12-20 12:23:20");

        LocalDate localDate = LocalDate.of(2020, 12, 20);
        Assert.assertEquals(localDate, LocalDateTimeUtil.dateToLocalDate(date));

        LocalDateTime localDateTime = LocalDateTime.of(2020, 12, 20, 12, 23, 20);
        Assert.assertEquals(localDateTime, LocalDateTimeUtil.dateToLocalDateTime(date));

        Assert.assertEquals("2020-12-20 12:23:20", LocalDateTimeUtil.dateToString(date));

        Assert.assertEquals("2020-12-20 12:23:20.000", LocalDateTimeUtil.dateToString(date, new SimpleDateFormat(yMdHmsSSS)));
        Assert.assertEquals("2020-12-20 12:23:20", LocalDateTimeUtil.dateToString(date, new SimpleDateFormat(yMdHms)));
        Assert.assertEquals("2020-12-20 12:23", LocalDateTimeUtil.dateToString(date, new SimpleDateFormat(yMdHm)));
        Assert.assertEquals("2020-12-20 12", LocalDateTimeUtil.dateToString(date, new SimpleDateFormat(yMdH)));
        Assert.assertEquals("2020-12-20", LocalDateTimeUtil.dateToString(date, new SimpleDateFormat(yMd)));
        Assert.assertEquals("2020-12", LocalDateTimeUtil.dateToString(date, new SimpleDateFormat(yM)));
        Assert.assertEquals("2020", LocalDateTimeUtil.dateToString(date, new SimpleDateFormat(y)));

        Assert.assertEquals("2020-12-20 12:23:20.000", LocalDateTimeUtil.dateToString(date, yMdHmsSSS));
        Assert.assertEquals("2020-12-20 12:23:20", LocalDateTimeUtil.dateToString(date, yMdHms));
        Assert.assertEquals("2020-12-20 12:23", LocalDateTimeUtil.dateToString(date, yMdHm));
        Assert.assertEquals("2020-12-20 12", LocalDateTimeUtil.dateToString(date, yMdH));
        Assert.assertEquals("2020-12-20", LocalDateTimeUtil.dateToString(date, yMd));
        Assert.assertEquals("2020-12", LocalDateTimeUtil.dateToString(date, yM));
        Assert.assertEquals("2020", LocalDateTimeUtil.dateToString(date, y));
    }

    @Test
    public void timestampToOtherTest() {
        Date date = LocalDateTimeUtil.stringToDate("2020-12-20 12:23:20.123");
        Timestamp timestamp = new Timestamp(date.getTime());

        LocalDate localDate = LocalDate.of(2020, 12, 20);
        Assert.assertEquals(localDate, LocalDateTimeUtil.timestampToLocalDate(timestamp));

        Timestamp timestamp1 = Timestamp.valueOf("2020-12-20 12:23:20.000000123");
        LocalDateTime localDateTime = LocalDateTime.of(2020, 12, 20, 12, 23, 20, 123);
        Assert.assertEquals(localDateTime, LocalDateTimeUtil.timestampToLocalDateTime(timestamp1));
        Assert.assertEquals(Long.valueOf(1608438200000L), LocalDateTimeUtil.timestampToLong(timestamp1));

        Assert.assertEquals("2020-12-20 12:23:20.000", LocalDateTimeUtil.timestampToString(timestamp1));

        Assert.assertEquals("2020-12-20 12:23:20.000", LocalDateTimeUtil.timestampToString(timestamp1, new SimpleDateFormat(yMdHmsSSS)));
        Assert.assertEquals("2020-12-20 12:23:20", LocalDateTimeUtil.timestampToString(timestamp1, new SimpleDateFormat(yMdHms)));
        Assert.assertEquals("2020-12-20 12:23", LocalDateTimeUtil.timestampToString(timestamp1, new SimpleDateFormat(yMdHm)));
        Assert.assertEquals("2020-12-20 12", LocalDateTimeUtil.timestampToString(timestamp1, new SimpleDateFormat(yMdH)));
        Assert.assertEquals("2020-12-20", LocalDateTimeUtil.timestampToString(timestamp1, new SimpleDateFormat(yMd)));
        Assert.assertEquals("2020-12", LocalDateTimeUtil.timestampToString(timestamp1, new SimpleDateFormat(yM)));
        Assert.assertEquals("2020", LocalDateTimeUtil.timestampToString(timestamp1, new SimpleDateFormat(y)));

        Assert.assertEquals("2020-12-20 12:23:20.000", LocalDateTimeUtil.timestampToString(timestamp1, yMdHmsSSS));
        Assert.assertEquals("2020-12-20 12:23:20", LocalDateTimeUtil.timestampToString(timestamp1, yMdHms));
        Assert.assertEquals("2020-12-20 12:23", LocalDateTimeUtil.timestampToString(timestamp1, yMdHm));
        Assert.assertEquals("2020-12-20 12", LocalDateTimeUtil.timestampToString(timestamp1, yMdH));
        Assert.assertEquals("2020-12-20", LocalDateTimeUtil.timestampToString(timestamp1, yMd));
        Assert.assertEquals("2020-12", LocalDateTimeUtil.timestampToString(timestamp1, yM));
        Assert.assertEquals("2020", LocalDateTimeUtil.timestampToString(timestamp1, y));
    }

    @Test
    public void stringToOtherTest() {
        String timeStr = "2020-12-20 12:23:20.123";

        LocalDateTimeUtil.stringToDate(timeStr);
        Date date = LocalDateTimeUtil.stringToDate("2020-12-20 12:23:20.123");
        Assert.assertEquals(date, LocalDateTimeUtil.stringToDate(timeStr));

        Time time = Time.valueOf("12:23:20");
        Assert.assertEquals(time, LocalDateTimeUtil.stringToTime("12:23:20"));

        LocalDate localDate = LocalDate.of(2020, 12, 20);
        Assert.assertEquals(localDate, LocalDateTimeUtil.stringToLocalDate("2020-12-20"));


        LocalDateTime localDateTime1 = LocalDateTime.of(2020, 10, 17, 11, 26, 33);
        Assert.assertEquals(localDateTime1, LocalDateTimeUtil.stringToLocalDateTime("2020-10-17 11:26:33"));

        LocalDateTime localDateTime2 = LocalDateTime.of(2020, 10, 17, 11, 26, 33, 0);
        Assert.assertEquals(localDateTime2, LocalDateTimeUtil.stringToLocalDateTime("2020-10-17 11:26:33.0"));


        LocalTime localTime1 = LocalTime.of(12, 23, 20, 123);
        Assert.assertEquals(localTime1, LocalDateTimeUtil.stringToLocalTime("12:23:20.000000123", "HH:mm:ss.SSSSSSSSS"));

        LocalTime localTime11 = LocalTime.of(12, 23, 20, 0);
        Assert.assertEquals(localTime11, LocalDateTimeUtil.stringToLocalTime("12:23:20.000", "HH:mm:ss.SSS"));

        LocalTime localTime2 = LocalTime.of(12, 23, 20);
        Assert.assertEquals(localTime2, LocalDateTimeUtil.stringToLocalTime("12:23:20", "HH:mm:ss"));

        LocalTime localTime3 = LocalTime.of(12, 23);
        Assert.assertEquals(localTime3, LocalDateTimeUtil.stringToLocalTime("12:23", "HH:mm"));

        Assert.assertEquals(localTime1, LocalDateTimeUtil.stringToLocalTime("12:23:20.000000123", HmsSSSMore));
        Assert.assertEquals(localTime11, LocalDateTimeUtil.stringToLocalTime("12:23:20.000", HmsSSS));
        Assert.assertEquals(localTime2, LocalDateTimeUtil.stringToLocalTime("12:23:20", Hms));
        Assert.assertEquals(localTime3, LocalDateTimeUtil.stringToLocalTime("12:23", Hm));

        Timestamp timestamp = Timestamp.valueOf("2020-12-20 12:23:20.000000123");
        Assert.assertEquals(timestamp, LocalDateTimeUtil.stringToTimestamp("2020-12-20 12:23:20.000000123"));
    }
}
