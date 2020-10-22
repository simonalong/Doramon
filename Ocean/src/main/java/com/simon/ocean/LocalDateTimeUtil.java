package com.simon.ocean;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 时间格式的各种转换
 * <p>
 *     <ul>
 *         1.LocalDateTime ----> LocalDate
 *         2.LocalDateTime ----> Long
 *         3.LocalDateTime ----> Date
 *         4.LocalDateTime ----> String
 *
 *         1.LocalDate ----> LocalDateTime
 *         2.LocalDate ----> Long
 *         3.LocalDate ----> Date
 *         4.LocalDate ----> String
 *
 *         1.Date ----> LocalDateTime
 *         2.Date ----> LocalDate
 *         3.Date ----> Long
 *         4.Date ----> String
 *
 *         1.Timestamp ----> LocalDateTime
 *         2.Timestamp ----> Long
 *         3.Timestamp ----> String
 *         4.Timestamp ----> LocalDate
 *
 *         1.String ----> LocalDateTime
 *         2.String ----> LocalDate
 *         3.String ----> Date
 *         4.String ----> Timestamp
 *         5.String ----> LocalTime
 *         6.String ----> Time
 *
 *         1.Long ----> Date
 *         2.Long ----> LocalDateTime
 *         3.Long ----> LocalDate
 *     </ul>
 * @author shizi
 * @since 2020/9/9 8:58 下午
 */
public class LocalDateTimeUtil {

    public static final String yMdHmsSSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String yMdHmsS = "yyyy-MM-dd HH:mm:ss.S";
    public static final String yMdHms = "yyyy-MM-dd HH:mm:ss";
    public static final String yMdHm = "yyyy-MM-dd HH:mm";
    public static final String yMdH = "yyyy-MM-dd HH";
    public static final String yMd = "yyyy-MM-dd";
    public static final String yM = "yyyy-MM";
    public static final String y = "yyyy";

    public static final String HmsSSSMore = "HH:mm:ss.SSSSSSSSS";
    public static final String HmsSSS = "HH:mm:ss.SSS";
    public static final String Hms = "HH:mm:ss";
    public static final String Hm = "HH:mm";
    public static final String H = "HH";

    private static final Pattern yMdHmsSSSPattern = Pattern.compile("^(\\d){4}-(\\d){2}-(\\d){2} (\\d){2}:(\\d){2}:(\\d){2}.(\\d){3}$");
    private static final Pattern yMdHmsSPattern = Pattern.compile("^(\\d){4}-(\\d){2}-(\\d){2} (\\d){2}:(\\d){2}:(\\d){2}.(\\d){1}$");
    private static final Pattern yMdHmsPattern = Pattern.compile("^(\\d){4}-(\\d){2}-(\\d){2} (\\d){2}:(\\d){2}:(\\d){2}$");
    private static final Pattern yMdHmPattern = Pattern.compile("^(\\d){4}-(\\d){2}-(\\d){2} (\\d){2}:(\\d){2}$");
    private static final Pattern yMdHPattern = Pattern.compile("^(\\d){4}-(\\d){2}-(\\d){2} (\\d){2}$");
    private static final Pattern yMdPattern = Pattern.compile("^(\\d){4}-(\\d){2}-(\\d){2}$");
    private static final Pattern yMPattern = Pattern.compile("^(\\d){4}-(\\d){2}$");
    private static final Pattern yPattern = Pattern.compile("^(\\d){4}$");

    private static final Map<String, SimpleDateFormat> simpleDateFormat = new ConcurrentHashMap<>(7);
    private static final Map<String, DateTimeFormatter> localDateTimeFormat = new ConcurrentHashMap<>(7);

    static {
        simpleDateFormat.put(yMdHmsSSS, new SimpleDateFormat(yMdHmsSSS));
        simpleDateFormat.put(yMdHms, new SimpleDateFormat(yMdHms));
        simpleDateFormat.put(yMdHm, new SimpleDateFormat(yMdHm));
        simpleDateFormat.put(yMdH, new SimpleDateFormat(yMdH));
        simpleDateFormat.put(yMd, new SimpleDateFormat(yMd));
        simpleDateFormat.put(yM, new SimpleDateFormat(yM));
        simpleDateFormat.put(y, new SimpleDateFormat(y));

        localDateTimeFormat.put(yMdHmsSSS, DateTimeFormatter.ofPattern(yMdHmsSSS));
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
    private static LocalDate localDateTimeToLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    /**
     * LocalDateTime 转 Long
     */
    public static Long localDateTimeToLong(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * LocalDateTime 转 Date
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime 转 String
     */
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime.format(localDateTimeFormat.get(yMdHms));
    }


    /**
     * LocalDateTime 转 String
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, String dateTimeFormat) {
        return localDateTime.format(localDateTimeFormat.get(dateTimeFormat));
    }

    /**
     * LocalDateTime 转 String
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        return localDateTime.format(dateTimeFormatter);
    }


    /**
     * LocalDate 转 LocalDateTime
     */
    public static LocalDateTime localDateToLocalDateTime(LocalDate localDate) {
        return LocalDateTime.of(localDate, LocalTime.parse("00:00:00"));
    }

    /**
     * LocalDate 转 Long
     */
    public static Long localDateToLong(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * LocalDate 转 Date
     */
    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDate 转 String
     */
    public static String localDateToString(LocalDate localDate) {
        return localDate.format(localDateTimeFormat.get(yMd));
    }


    /**
     * Date 转 LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Date 转 Long
     */
    public static Long dateToLong(Date date) {
        return date.getTime();
    }

    /**
     * Date 转 LocalDate
     */
    public static LocalDate dateToLocalDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Date 转 String
     */
    public static String dateToString(Date date) {
        return simpleDateFormat.get(yMdHms).format(date);
    }

    /**
     * Date 转 String
     */
    public static String dateToString(Date date, SimpleDateFormat simpleDateFormat) {
        return simpleDateFormat.format(date);
    }

    /**
     * Date 转 String
     */
    public static String dateToString(Date date, String simpleDateFormatStr) {
        return simpleDateFormat.get(simpleDateFormatStr).format(date);
    }


    /**
     * Timestamp 转 LocalDateTime
     */
    public static LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        return timestamp.toLocalDateTime();
    }

    /**
     * Timestamp 转 Long
     */
    public static Long timestampToLong(Timestamp timestamp) {
        return timestamp.getTime();
    }

    /**
     * Timestamp 转 LocalDate
     */
    public static LocalDate timestampToLocalDate(Timestamp timestamp) {
        return LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Timestamp 转 String
     */
    public static String timestampToString(Timestamp timestamp) {
        return simpleDateFormat.get(yMdHmsSSS).format(timestamp);
    }

    /**
     * Timestamp 转 String
     */
    public static String timestampToString(Timestamp timestamp, SimpleDateFormat simpleDateFormat) {
        return simpleDateFormat.format(timestamp);
    }

    /**
     * Timestamp 转 String
     */
    public static String timestampToString(Timestamp timestamp, String simpleDateFormatStr) {
        return simpleDateFormat.get(simpleDateFormatStr).format(timestamp);
    }


    /**
     * String 转 LocalDateTime
     */
    public static LocalDateTime stringToLocalDateTime(String strDateTime) {
        return LocalDateTime.parse(strDateTime, DateTimeFormatter.ofPattern(getTimeFormat(strDateTime)));
    }

    /**
     * String 转 LocalDateTime
     */
    public static LocalDateTime stringToLocalDateTime(String strDateTime, String formatStr) {
        return LocalDateTime.parse(strDateTime, DateTimeFormatter.ofPattern(formatStr));
    }

    /**
     * String 转 LocalDate
     */
    public static LocalDate stringToLocalDate(String strDateTime) {
        return LocalDate.parse(strDateTime, localDateTimeFormat.get(yMd));
    }

    /**
     * String 转 Date
     */
    public static Date stringToDate(String strDateTime) {
        try {
            return simpleDateFormat.get(getTimeFormat(strDateTime)).parse(strDateTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * String 转 Timestamp
     */
    public static Timestamp stringToTimestamp(String strDateTime) {
        return Timestamp.valueOf(strDateTime);
    }

    /**
     * String 转 LocalTime
     */
    public static LocalTime stringToLocalTime(String strDateTime, String datetimeFormat) {
        return LocalTime.parse(strDateTime, DateTimeFormatter.ofPattern(datetimeFormat));
    }

    /**
     * String 转 LocalTime
     */
    public static LocalTime stringToLocalTime(String strDateTime) {
        return LocalTime.parse(strDateTime, DateTimeFormatter.ofPattern(HmsSSS));
    }

    /**
     * String 转 Time
     */
    public static Time stringToTime(String strDateTime) {
        return Time.valueOf(strDateTime);
    }

    /**
     * Long 转 Date
     */
    public static Date longToDate(Long time) {
        return new Date(time);
    }

    /**
     * Long 转 LocalDateTime
     */
    public static LocalDateTime longToLocalDateTime(Long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    /**
     * Long 转 LocalDate
     */
    public static LocalDate longToLocalDate(Long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time),ZoneId.systemDefault()).toLocalDate();
    }

    private static String getTimeFormat(String strDateTime) {
        if (null == strDateTime) {
            throw new RuntimeException("获取时间格式错误, time =" + strDateTime);
        }
        String data;
        data = strDateTime.trim();
        if ("".equals(data) || "null".equals(data)){
            throw new RuntimeException("获取时间格式错误, time =" + strDateTime);
        }
        String timeFormat = yMdHms;
        if (yPattern.matcher(data).matches()) {
            timeFormat = y;
        } else if (yMPattern.matcher(data).matches()) {
            timeFormat = yM;
        } else if (yMdPattern.matcher(data).matches()) {
            timeFormat = yMd;
        } else if (yMdHPattern.matcher(data).matches()) {
            timeFormat = yMdH;
        } else if (yMdHmPattern.matcher(data).matches()) {
            timeFormat = yMdHm;
        } else if (yMdHmsPattern.matcher(data).matches()) {
            timeFormat = yMdHms;
        } else if (yMdHmsSPattern.matcher(data).matches()) {
            timeFormat = yMdHmsS;
        } else if (yMdHmsSSSPattern.matcher(data).matches()) {
            timeFormat = yMdHmsSSS;
        }
        return timeFormat;
    }
}
