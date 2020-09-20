package com.simon.ocean;

import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shizi
 * @since 2020/9/9 8:58 下午
 */
@UtilityClass
public class LocalDateTimeUtil {

    public final String yMdHmsS = "yyyy-MM-dd HH:mm:ss SSS";
    public final String yMdHms = "yyyy-MM-dd HH:mm:ss";
    public final String yMdHm = "yyyy-MM-dd HH:mm";
    public final String yMdH = "yyyy-MM-dd HH";
    public final String yMd = "yyyy-MM-dd";
    public final String yM = "yyyy-MM";
    public final String y = "yyyy";

    private final Map<String, SimpleDateFormat> simpleDateFormat = new ConcurrentHashMap<>(7);
    private final Map<String, DateTimeFormatter> localDateTimeFormat = new ConcurrentHashMap<>(7);

    static {
        simpleDateFormat.put(yMdHmsS, new SimpleDateFormat(yMdHmsS));
        simpleDateFormat.put(yMdHms, new SimpleDateFormat(yMdHms));
        simpleDateFormat.put(yMdHm, new SimpleDateFormat(yMdHm));
        simpleDateFormat.put(yMdH, new SimpleDateFormat(yMdH));
        simpleDateFormat.put(yMd, new SimpleDateFormat(yMd));
        simpleDateFormat.put(yM, new SimpleDateFormat(yM));
        simpleDateFormat.put(y, new SimpleDateFormat(y));

        localDateTimeFormat.put(yMdHmsS, DateTimeFormatter.ofPattern(yMdHmsS));
        localDateTimeFormat.put(yMdHms, DateTimeFormatter.ofPattern(yMdHms));
        localDateTimeFormat.put(yMdHm, DateTimeFormatter.ofPattern(yMdHm));
        localDateTimeFormat.put(yMdH, DateTimeFormatter.ofPattern(yMdH));
        localDateTimeFormat.put(yMd, DateTimeFormatter.ofPattern(yMd));
        localDateTimeFormat.put(yM, DateTimeFormatter.ofPattern(yM));
        localDateTimeFormat.put(y, DateTimeFormatter.ofPattern(y));
    }

    /**
     * LocalDateTime 转 LocalDate
     */
    private LocalDate localDateTimeToLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    /**
     * LocalDateTime 转 Long
     */
    public Long localDateTimeToLong(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * LocalDateTime 转 Date
     */
    public Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime 转 String
     */
    public String localDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime.format(localDateTimeFormat.get(yMdHms));
    }

    /**
     * 根据指定的格式：LocalDateTime 转 String
     */
    public String localDateTimeToString(LocalDateTime localDateTime, String dateTimeFormat) {
        return localDateTime.format(localDateTimeFormat.get(dateTimeFormat));
    }

    /**
     * 根据指定的格式：LocalDateTime 转 String
     */
    public String localDateTimeToString(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        return localDateTime.format(dateTimeFormatter);
    }


    /**
     * LocalDate 转 LocalDateTime
     */
    public LocalDateTime localDateToLocalDateTime(LocalDate localDate) {
        return LocalDateTime.of(localDate, LocalTime.parse("00:00:00"));
    }

    /**
     * LocalDate 转 Long
     */
    public Long localDateToLong(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * LocalDate 转 Date
     */
    public Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDate 转 String
     */
    public String localDateToString(LocalDate localDate) {
        return localDate.format(localDateTimeFormat.get(yMd));
    }


    /**
     * Date 转 LocalDateTime
     */
    public LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Date 转 Long
     */
    public Long dateToLong(Date date) {
        return date.getTime();
    }

    /**
     * Date 转 LocalDate
     */
    public LocalDate dateToLocalDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Date 转 String
     */
    public String dateToString(Date date) {
        return simpleDateFormat.get(yMdHms).format(date);
    }

    /**
     * Date 转 String
     */
    public String dateToString(Date date, SimpleDateFormat simpleDateFormat) {
        return simpleDateFormat.format(date);
    }

    /**
     * Date 转 String
     */
    public String dateToString(Date date, String simpleDateFormatStr) {
        return simpleDateFormat.get(simpleDateFormatStr).format(date);
    }


    /**
     * String 转 LocalDateTime
     */
    public LocalDateTime stringToLocalDateTime(String strDateTime) {
        return LocalDateTime.parse(strDateTime, localDateTimeFormat.get(yMdHms));
    }

    /**
     * String 转 LocalDate
     */
    public LocalDateTime stringToLocalDate(String strDateTime) {
        return LocalDateTime.parse(strDateTime, localDateTimeFormat.get(yMd));
    }

    /**
     * String 转 Date
     */
    public Date stringToDate(String strDateTime) {
        return Date.from(LocalDateTime.parse(strDateTime, localDateTimeFormat.get(yMdHms)).atZone(ZoneId.systemDefault()).toInstant());
    }


    /**
     * Long 转 LocalDateTime
     */
    public LocalDateTime longToLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * Long 转 LocalDate
     */
    public LocalDate longToLocalDate(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp),ZoneId.systemDefault()).toLocalDate();
    }
}
