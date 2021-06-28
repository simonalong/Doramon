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
 *
 * <ul>
 *     <li>1.1.LocalDateTime {@code ---->} LocalDate</li>
 *     <li>1.2.LocalDateTime {@code ---->} Long</li>
 *     <li>1.3.LocalDateTime {@code ---->} Date</li>
 *     <li>1.4.LocalDateTime {@code ---->} String</li>
 *     <li>2.1.LocalDate {@code ---->} LocalDateTime</li>
 *     <li>2.2.LocalDate {@code ---->} Long</li>
 *     <li>2.3.LocalDate {@code ---->} Date</li>
 *     <li>2.4.LocalDate {@code ---->} String</li>
 *     <li>3.1.Date {@code ---->} LocalDateTime</li>
 *     <li>3.2.Date {@code ---->} LocalDate</li>
 *     <li>3.3.Date {@code ---->} Long</li>
 *     <li>3.4.Date {@code ---->} String</li>
 *     <li>4.1.Timestamp {@code ---->} LocalDateTime</li>
 *     <li>4.2.Timestamp {@code ---->} Long</li>
 *     <li>4.3.Timestamp {@code ---->} String</li>
 *     <li>4.4.Timestamp {@code ---->} LocalDate</li>
 *     <li>5.1.String {@code ---->} LocalDateTime</li>
 *     <li>5.2.String {@code ---->} LocalDate</li>
 *     <li>5.3.String {@code ---->} Date</li>
 *     <li>5.4.String {@code ---->} Timestamp</li>
 *     <li>5.5.String {@code ---->} LocalTime</li>
 *     <li>5.6.String {@code ---->} Time</li>
 *     <li>6.1.Long {@code ---->} Date</li>
 *     <li>6.2.Long {@code ---->} LocalDateTime</li>
 *     <li>6.3.Long {@code ---->} LocalDate</li>
 *     <li>6.4.Long {@code ---->} String</li>
 * </ul>
 *
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
     *
     * @param localDateTime 待转换时间
     * @return 转换后的时间
     */
    private static LocalDate localDateTimeToLocalDate(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        return localDateTime.toLocalDate();
    }

    /**
     * LocalDateTime 转 Long
     *
     * @param localDateTime 待转换时间
     * @return 转换后的时间
     */
    public static Long localDateTimeToLong(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * LocalDateTime 转 Date
     *
     * @param localDateTime 待转换时间
     * @return 转换后的时间
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime 转 String
     *
     * @param localDateTime 待转换时间
     * @return 转换后的时间
     */
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        return localDateTime.format(localDateTimeFormat.get(yMdHms));
    }


    /**
     * LocalDateTime 转 String
     *
     * @param localDateTime  待转换时间
     * @param dateTimeFormat 时间转换格式，建议使用{@link LocalDateTimeUtil#yMdHmsSSS}或者{@link LocalDateTimeUtil#yMdHms}等字段
     * @return 转换后的时间
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, String dateTimeFormat) {
        if (null == localDateTime || null == dateTimeFormat) {
            return null;
        }
        return localDateTime.format(localDateTimeFormat.get(dateTimeFormat));
    }

    /**
     * LocalDateTime 转 String
     *
     * @param localDateTime     待转换时间
     * @param dateTimeFormatter 时间转换格式器
     * @return 转换后的时间
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        if (null == localDateTime || null == dateTimeFormatter) {
            return null;
        }
        return localDateTime.format(dateTimeFormatter);
    }


    /**
     * LocalDate 转 LocalDateTime
     *
     * @param localDate 待转换时间
     * @return 转换后的时间
     */
    public static LocalDateTime localDateToLocalDateTime(LocalDate localDate) {
        if (null == localDate) {
            return null;
        }
        return LocalDateTime.of(localDate, LocalTime.parse("00:00:00"));
    }

    /**
     * LocalDate 转 Long
     *
     * @param localDate 待转换时间
     * @return 转换后的时间
     */
    public static Long localDateToLong(LocalDate localDate) {
        if (null == localDate) {
            return null;
        }
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * LocalDate 转 Date
     *
     * @param localDate 待转换时间
     * @return 转换后的时间
     */
    public static Date localDateToDate(LocalDate localDate) {
        if (null == localDate) {
            return null;
        }
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDate 转 String
     *
     * @param localDate 待转换时间
     * @return 转换后的时间
     */
    public static String localDateToString(LocalDate localDate) {
        if (null == localDate) {
            return null;
        }
        return localDate.format(localDateTimeFormat.get(yMd));
    }

    /**
     * LocalDate 转 String
     *
     * @param localTime 待转换时间
     * @return 转换后的时间
     */
    public static String localTimeToString(LocalTime localTime) {
        if (null == localTime) {
            return null;
        }
        return localTime.format(localDateTimeFormat.get(HmsSSS));
    }

    /**
     * Date 转 LocalDateTime
     *
     * @param date 待转换时间
     * @return 转换后的时间
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (null == date) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Date 转 Long
     *
     * @param date 待转换时间
     * @return 转换后的时间
     */
    public static Long dateToLong(Date date) {
        if (null == date) {
            return null;
        }
        return date.getTime();
    }

    /**
     * Date 转 LocalDate
     *
     * @param date 待转换时间
     * @return 转换后的时间
     */
    public static LocalDate dateToLocalDate(Date date) {
        if (null == date) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Date 转 String
     *
     * @param date 待转换时间
     * @return 转换后的时间
     */
    public static String dateToString(Date date) {
        if (null == date) {
            return null;
        }
        return simpleDateFormat.get(yMdHms).format(date);
    }

    /**
     * Date 转 String
     *
     * @param date             待转换时间
     * @param simpleDateFormat 基本的格式转换器
     * @return 转换后的时间
     */
    public static String dateToString(Date date, SimpleDateFormat simpleDateFormat) {
        if (null == date || null == simpleDateFormat) {
            return null;
        }
        return simpleDateFormat.format(date);
    }

    /**
     * Date 转 String
     *
     * @param date                待转换时间
     * @param simpleDateFormatStr 时间转换格式，建议使用{@link LocalDateTimeUtil#yMdHmsSSS}或者{@link LocalDateTimeUtil#yMdHms}等字段
     * @return 转换后的时间
     */
    public static String dateToString(Date date, String simpleDateFormatStr) {
        if (null == date || null == simpleDateFormatStr) {
            return null;
        }
        return simpleDateFormat.get(simpleDateFormatStr).format(date);
    }


    /**
     * Timestamp 转 LocalDateTime
     *
     * @param timestamp 待转换时间
     * @return 转换后的时间
     */
    public static LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        if (null == timestamp) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }

    /**
     * Timestamp 转 Long
     *
     * @param timestamp 待转换时间
     * @return 转换后的时间
     */
    public static Long timestampToLong(Timestamp timestamp) {
        if (null == timestamp) {
            return null;
        }
        return timestamp.getTime();
    }

    /**
     * Timestamp 转 LocalDate
     *
     * @param timestamp 待转换时间
     * @return 转换后的时间
     */
    public static LocalDate timestampToLocalDate(Timestamp timestamp) {
        if (null == timestamp) {
            return null;
        }
        return LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Timestamp 转 String
     *
     * @param timestamp 待转换时间
     * @return 转换后的时间
     */
    public static String timestampToString(Timestamp timestamp) {
        if (null == timestamp) {
            return null;
        }
        return simpleDateFormat.get(yMdHmsSSS).format(timestamp);
    }

    /**
     * Timestamp 转 String
     *
     * @param timestamp        待转换时间
     * @param simpleDateFormat 简单的时间转换格式
     * @return 转换后的时间
     */
    public static String timestampToString(Timestamp timestamp, SimpleDateFormat simpleDateFormat) {
        if (null == timestamp || null == simpleDateFormat) {
            return null;
        }
        return simpleDateFormat.format(timestamp);
    }

    /**
     * Timestamp 转 String
     *
     * @param timestamp           待转换时间
     * @param simpleDateFormatStr 时间转换格式，建议使用{@link LocalDateTimeUtil#yMdHmsSSS}或者{@link LocalDateTimeUtil#yMdHms}等字段
     * @return 转换后的时间
     */
    public static String timestampToString(Timestamp timestamp, String simpleDateFormatStr) {
        if (null == timestamp || null == simpleDateFormatStr) {
            return null;
        }
        return simpleDateFormat.get(simpleDateFormatStr).format(timestamp);
    }


    /**
     * String 转 LocalDateTime
     *
     * @param strDateTime 待转换时间
     * @return 转换后的时间
     */
    public static LocalDateTime stringToLocalDateTime(String strDateTime) {
        if (null == strDateTime) {
            return null;
        }
        return LocalDateTime.parse(strDateTime, DateTimeFormatter.ofPattern(getTimeFormat(strDateTime)));
    }

    /**
     * String 转 LocalDateTime
     *
     * @param strDateTime 待转换时间
     * @param formatStr   时间格式
     * @return 转换后的时间
     */
    public static LocalDateTime stringToLocalDateTime(String strDateTime, String formatStr) {
        if (null == strDateTime || null == formatStr) {
            return null;
        }
        return LocalDateTime.parse(strDateTime, DateTimeFormatter.ofPattern(formatStr));
    }

    /**
     * String 转 LocalDate
     *
     * @param strDateTime 待转换时间
     * @return 转换后的时间
     */
    public static LocalDate stringToLocalDate(String strDateTime) {
        if (null == strDateTime) {
            return null;
        }
        return LocalDate.parse(strDateTime, localDateTimeFormat.get(yMd));
    }

    /**
     * String 转 Date
     *
     * @param strDateTime 待转换时间
     * @return 转换后的时间
     */
    public static Date stringToDate(String strDateTime) {
        if (null == strDateTime) {
            return null;
        }
        try {
            return simpleDateFormat.get(getTimeFormat(strDateTime)).parse(strDateTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * String 转 Timestamp
     *
     * @param strDateTime 待转换时间
     * @return 转换后的时间
     */
    public static Timestamp stringToTimestamp(String strDateTime) {
        if (null == strDateTime) {
            return null;
        }
        return Timestamp.valueOf(strDateTime);
    }

    /**
     * String 转 LocalTime
     *
     * @param strDateTime    待转换时间
     * @param datetimeFormat 时间格式
     * @return 转换后的时间
     */
    public static LocalTime stringToLocalTime(String strDateTime, String datetimeFormat) {
        if (null == strDateTime || null == datetimeFormat) {
            return null;
        }
        return LocalTime.parse(strDateTime, DateTimeFormatter.ofPattern(datetimeFormat));
    }

    /**
     * String 转 LocalTime
     *
     * @param strDateTime 待转换时间
     * @return 转换后的时间
     */
    public static LocalTime stringToLocalTime(String strDateTime) {
        if (null == strDateTime) {
            return null;
        }
        return LocalTime.parse(strDateTime, DateTimeFormatter.ofPattern(HmsSSS));
    }

    /**
     * String 转 Time
     *
     * @param strDateTime 待转换时间
     * @return 转换后的时间
     */
    public static Time stringToTime(String strDateTime) {
        if (null == strDateTime) {
            return null;
        }
        return Time.valueOf(strDateTime);
    }

    /**
     * Long 转 Date
     *
     * @param time 待转换时间
     * @return 转换后的时间
     */
    public static Date longToDate(Long time) {
        if (null == time) {
            return null;
        }
        return new Date(time);
    }

    /**
     * Long 转 LocalDateTime
     *
     * @param time 待转换时间
     * @return 转换后的时间
     */
    public static LocalDateTime longToLocalDateTime(Long time) {
        if (null == time) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    /**
     * Long 转 LocalDate
     *
     * @param time 待转换时间
     * @return 转换后的时间
     */
    public static LocalDate longToLocalDate(Long time) {
        if (null == time) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Long 转 String
     *
     * @param time 待转换时间
     * @return 转换后的时间
     */
    public static String longToString(Long time) {
        if (null == time) {
            return null;
        }
        return simpleDateFormat.get(yMdHms).format(new Date(time));
    }

    /**
     * Long 转 String
     *
     * @param time      待转换时间
     * @param formatKey 时间转换格式，建议使用{@link LocalDateTimeUtil#yMdHmsSSS}或者{@link LocalDateTimeUtil#yMdHms}等字段
     * @return 转换后的时间
     */
    public static String longToString(Long time, String formatKey) {
        if (null == time || null == formatKey) {
            return null;
        }
        return simpleDateFormat.get(formatKey).format(new Date(time));
    }


    public static Date setTime(Date date, Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second) {
        if (null == date) {
            return null;
        }

        Integer yearCopy = year;
        if (null == year) {
            yearCopy = 0;
        }

        Integer monthCopy = month;
        if (null == month) {
            monthCopy = 0;
        }

        Integer dayCopy = day;
        if (null == day) {
            dayCopy = 0;
        }

        Integer hourCopy = hour;
        if (null == hour) {
            hourCopy = 0;
        }

        Integer minuteCopy = minute;
        if (null == minute) {
            minuteCopy = 0;
        }

        Integer secondCopy = second;
        if (null == second) {
            secondCopy = 0;
        }
        return localDateTimeToDate(
            dateToLocalDateTime(date).withYear(yearCopy).withMonth(monthCopy).withDayOfMonth(dayCopy).withHour(hourCopy).withMinute(minuteCopy).withSecond(secondCopy));
    }

    public static Date setYear(Date date, Integer year) {
        if (null == date) {
            return null;
        }

        Integer yearCopy = year;
        if (null == year) {
            yearCopy = 0;
        }
        return localDateTimeToDate(dateToLocalDateTime(date).withYear(yearCopy));
    }

    public static Date setMonth(Date date, Integer month) {
        if (null == date) {
            return null;
        }
        Integer monthCopy = month;
        if (null == month) {
            monthCopy = 0;
        }
        return localDateTimeToDate(dateToLocalDateTime(date).withMonth(monthCopy));
    }

    public static Date setDay(Date date, Integer day) {
        if (null == date) {
            return null;
        }
        Integer dayCopy = day;
        if (null == day) {
            dayCopy = 0;
        }
        return localDateTimeToDate(dateToLocalDateTime(date).withDayOfMonth(dayCopy));
    }

    public static Date setHour(Date date, Integer hour) {
        if (null == date) {
            return null;
        }

        Integer hourCopy = hour;
        if (null == hour) {
            hourCopy = 0;
        }
        return localDateTimeToDate(dateToLocalDateTime(date).withHour(hourCopy));
    }

    public static Date setMinute(Date date, Integer minute) {
        if (null == date) {
            return null;
        }

        Integer minuteCopy = minute;
        if (null == minute) {
            minuteCopy = 0;
        }

        return localDateTimeToDate(dateToLocalDateTime(date).withMinute(minuteCopy));
    }

    public static Date setSecond(Date date, Integer second) {
        if (null == date) {
            return null;
        }

        Integer secondCopy = second;
        if (null == second) {
            secondCopy = 0;
        }
        return localDateTimeToDate(dateToLocalDateTime(date).withSecond(secondCopy));
    }

    public static Long setTime(Long time, Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second) {
        if (null == time || null == year || null == month || null == day || null == hour || null == minute || null == second) {
            return null;
        }
        return localDateTimeToLong(longToLocalDateTime(time).withYear(year).withMonth(month).withDayOfMonth(day).withHour(hour).withMinute(minute).withSecond(second));
    }

    public static Long setYear(Long time, Integer year) {
        if (null == time || null == year) {
            return null;
        }
        return localDateTimeToLong(longToLocalDateTime(time).withYear(year));
    }

    public static Long setMonth(Long time, Integer month) {
        if (null == time || null == month) {
            return null;
        }
        return localDateTimeToLong(longToLocalDateTime(time).withMonth(month));
    }

    public static Long setDay(Long time, Integer day) {
        if (null == time || null == day) {
            return null;
        }
        return localDateTimeToLong(longToLocalDateTime(time).withDayOfMonth(day));
    }

    public static Long setHour(Long time, Integer hour) {
        if (null == time || null == hour) {
            return null;
        }
        return localDateTimeToLong(longToLocalDateTime(time).withHour(hour));
    }

    public static Long setMinute(Long time, Integer minute) {
        if (null == time || null == minute) {
            return null;
        }
        return localDateTimeToLong(longToLocalDateTime(time).withMinute(minute));
    }

    public static Long setSecond(Long time, Integer second) {
        if (null == time || null == second) {
            return null;
        }
        return localDateTimeToLong(longToLocalDateTime(time).withSecond(second));
    }

    public static Date plusTime(Date date, Integer years, Integer months, Integer days, Integer hours, Integer minutes, Integer seconds) {
        if (null == date) {
            return null;
        }

        Integer yearsCopy = years;
        if (null == years) {
            yearsCopy = 0;
        }

        Integer monthsCopy = months;
        if (null == months) {
            monthsCopy = 0;
        }

        Integer daysCopy = days;
        if (null == days) {
            daysCopy = 0;
        }

        Integer hoursCopy = hours;
        if (null == hours) {
            hoursCopy = 0;
        }

        Integer minutesCopy = minutes;
        if (null == minutes) {
            minutesCopy = 0;
        }

        Integer secondsCopy = seconds;
        if (null == seconds) {
            secondsCopy = 0;
        }
        return localDateTimeToDate(
            dateToLocalDateTime(date).plusYears(yearsCopy).plusMonths(monthsCopy).plusDays(daysCopy).plusHours(hoursCopy).plusMinutes(minutesCopy).plusSeconds(secondsCopy));
    }

    public static Date plusYears(Date date, Integer years) {
        if (null == date) {
            return null;
        }

        Integer yearsCopy = years;
        if (null == years) {
            yearsCopy = 0;
        }
        return localDateTimeToDate(dateToLocalDateTime(date).plusYears(yearsCopy));
    }

    public static Date plusMonths(Date date, Integer months) {
        if (null == date) {
            return null;
        }

        Integer monthsCopy = months;
        if (null == months) {
            monthsCopy = 0;
        }
        return localDateTimeToDate(dateToLocalDateTime(date).plusMonths(monthsCopy));
    }

    public static Date plusDays(Date date, Integer days) {
        if (null == date) {
            return null;
        }

        Integer daysCopy = days;
        if (null == days) {
            daysCopy = 0;
        }
        return localDateTimeToDate(dateToLocalDateTime(date).plusDays(daysCopy));
    }

    public static Date plusHours(Date date, Integer hours) {
        if (null == date) {
            return null;
        }

        Integer hoursCopy = hours;
        if (null == hours) {
            hoursCopy = 0;
        }
        return localDateTimeToDate(dateToLocalDateTime(date).plusHours(hoursCopy));
    }

    public static Date plusMinutes(Date date, Integer minutes) {
        if (null == date) {
            return null;
        }

        Integer minutesCopy = minutes;
        if (null == minutes) {
            minutesCopy = 0;
        }
        return localDateTimeToDate(dateToLocalDateTime(date).plusMinutes(minutesCopy));
    }

    public static Date plusSeconds(Date date, Integer seconds) {
        if (null == date) {
            return null;
        }

        Integer secondsCopy = seconds;
        if (null == seconds) {
            secondsCopy = 0;
        }
        return localDateTimeToDate(dateToLocalDateTime(date).plusSeconds(secondsCopy));
    }

    public static Long plusTime(Long time, Integer years, Integer months, Integer days, Integer hours, Integer minutes, Integer seconds) {
        if (null == time) {
            return null;
        }

        Integer yearsCopy = years;
        if (null == years) {
            yearsCopy = 0;
        }

        Integer monthsCopy = months;
        if (null == months) {
            monthsCopy = 0;
        }

        Integer daysCopy = days;
        if (null == days) {
            daysCopy = 0;
        }

        Integer hoursCopy = hours;
        if (null == hours) {
            hoursCopy = 0;
        }

        Integer minutesCopy = minutes;
        if (null == minutes) {
            minutesCopy = 0;
        }

        Integer secondsCopy = seconds;
        if (null == seconds) {
            secondsCopy = 0;
        }
        return localDateTimeToLong(
            longToLocalDateTime(time).plusYears(yearsCopy).plusMonths(monthsCopy).plusDays(daysCopy).plusHours(hoursCopy).plusMinutes(minutesCopy).plusSeconds(secondsCopy));
    }

    public static Long plusYears(Long time, Integer years) {
        if (null == time) {
            return null;
        }

        Integer yearsCopy = years;
        if (null == years) {
            yearsCopy = 0;
        }
        return localDateTimeToLong(longToLocalDateTime(time).plusYears(yearsCopy));
    }

    public static Long plusMonths(Long time, Integer months) {
        if (null == time) {
            return null;
        }

        Integer monthsCopy = months;
        if (null == months) {
            monthsCopy = 0;
        }
        return localDateTimeToLong(longToLocalDateTime(time).plusMonths(monthsCopy));
    }

    public static Long plusDays(Long time, Integer days) {
        if (null == time) {
            return null;
        }

        Integer daysCopy = days;
        if (null == days) {
            daysCopy = 0;
        }
        return localDateTimeToLong(longToLocalDateTime(time).plusDays(daysCopy));
    }

    public static Long plusHours(Long time, Integer hours) {
        if (null == time) {
            return null;
        }

        Integer hoursCopy = hours;
        if (null == hours) {
            hoursCopy = 0;
        }
        return localDateTimeToLong(longToLocalDateTime(time).plusHours(hoursCopy));
    }

    public static Long plusMinutes(Long time, Integer minutes) {
        if (null == time) {
            return null;
        }

        Integer minutesCopy = minutes;
        if (null == minutes) {
            minutesCopy = 0;
        }
        return localDateTimeToLong(longToLocalDateTime(time).plusMinutes(minutesCopy));
    }

    public static Long plusSeconds(Long time, Integer seconds) {
        if (null == time) {
            return null;
        }

        Integer secondsCopy = seconds;
        if (null == seconds) {
            secondsCopy = 0;
        }
        return localDateTimeToLong(longToLocalDateTime(time).plusSeconds(secondsCopy));
    }

    public static Date setDayStart(Date date) {
        if (null == date) {
            return null;
        }
        return localDateTimeToDate(dateToLocalDateTime(date).withHour(0).withMinute(0).withSecond(0).withNano(0));
    }

    public static Long setDayStart(Long time) {
        if (null == time) {
            return null;
        }
        return localDateTimeToLong(longToLocalDateTime(time).withHour(0).withMinute(0).withSecond(0).withNano(0));
    }

    public static Date setDayEnd(Date date) {
        if (null == date) {
            return null;
        }
        return localDateTimeToDate(dateToLocalDateTime(date).withHour(23).withMinute(59).withSecond(59));
    }

    public static Long setDayEnd(Long time) {
        if (null == time) {
            return null;
        }
        return localDateTimeToLong(longToLocalDateTime(time).withHour(23).withMinute(59).withSecond(59));
    }

    private static String getTimeFormat(String strDateTime) {
        if (null == strDateTime) {
            throw new RuntimeException("获取时间格式错误, time =" + strDateTime);
        }
        String data;
        data = strDateTime.trim();
        if ("".equals(data) || "null".equals(data)) {
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
