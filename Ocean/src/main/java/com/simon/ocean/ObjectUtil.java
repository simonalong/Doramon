package com.simon.ocean;

import com.alibaba.fastjson.JSON;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

/**
 * 将Object转换为指定的类型
 *
 * @author zhouzhenyong
 * @since 2019/5/4 下午12:30
 */
@Slf4j
@UtilityClass
public class ObjectUtil {

    private final String LOG_PRE = "[objectUtil] ";
    private final String NULL_STR = "null";

    public Boolean toBoolean(Object value) {
        if (null == value) {
            return null;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.valueOf(String.valueOf(value));
    }

    public Character toChar(Object value) {
        if (null == value) {
            return null;
        }

        if (value instanceof Character) {
            return (Character) value;
        }
        String valueStr = String.valueOf(value);
        if (valueStr.length() == 0) {
            return null;
        }
        return valueStr.charAt(0);
    }

    public String toStr(Object value) {
        if (null == value) {
            return null;
        }

        if (value instanceof String) {
            return (String) value;
        }
        return String.valueOf(value);
    }

    public Byte toByte(Object value) {
        if (null == value) {
            return null;
        }

        try {
            if (value instanceof Number) {
                return ((Number) value).byteValue();
            }
            return Byte.valueOf(String.valueOf(value));
        } catch (NumberFormatException | ClassCastException e) {
            log.error(LOG_PRE + "toByte error", e);
            throw e;
        }
    }

    public Short toShort(Object value) {
        if (null == value) {
            return null;
        }

        try {
            if (value instanceof Number) {
                return ((Number) value).shortValue();
            }
            return Short.valueOf(String.valueOf(value));
        } catch (NumberFormatException | ClassCastException e) {
            log.error(LOG_PRE + "toShort error", e);
            throw e;
        }
    }

    public Integer toInt(Object value) {
        if (null == value) {
            return null;
        }

        try {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.valueOf(String.valueOf(value));
        } catch (NumberFormatException | ClassCastException e) {
            log.error(LOG_PRE + "toInt error", e);
            throw e;
        }
    }

    public Long toLong(Object value) {
        if (null == value) {
            return null;
        }

        try {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }

            if (value instanceof java.sql.Date) {
                return ((java.sql.Date) value).getTime();
            }

            if (value instanceof java.sql.Time) {
                return ((java.sql.Time) value).getTime();
            }

            if (value instanceof java.sql.Timestamp) {
                return ((java.sql.Timestamp) value).getTime();
            }

            if (value instanceof Date) {
                return ((Date) value).getTime();
            }
            return Long.valueOf(String.valueOf(value));
        } catch (NumberFormatException | ClassCastException e) {
            log.error(LOG_PRE + "toLong error", e);
            throw e;
        }
    }

    public Double toDouble(Object value) {
        if (null == value) {
            return null;
        }

        try {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            return Double.valueOf(String.valueOf(value));
        } catch (NumberFormatException | ClassCastException e) {
            log.error(LOG_PRE + "toDouble error", e);
            throw e;
        }
    }

    public Float toFloat(Object value) {
        if (null == value) {
            return null;
        }

        try {
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            }
            return Float.valueOf(String.valueOf(value));
        } catch (NumberFormatException | ClassCastException e) {
            log.error(LOG_PRE + "toFloat error", e);
            throw e;
        }
    }

    public <T> T toEnum(Class<T> tClass, Object value) {
        if (null == tClass || null == value) {
            return null;
        }

        if (tClass.isEnum() && value instanceof String) {
            return Stream.of(tClass.getEnumConstants()).filter(t -> t.toString().equalsIgnoreCase((String) value)).findFirst().orElse(null);
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T[] toArray(Object value) {
        if (null == value) {
            return null;
        }

        if (value.getClass().isArray()) {
            return (T[]) value;
        }

        if (value instanceof Collection) {
            return (T[]) ((Collection) value).toArray();
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> List<T> toList(Object value) {
        if (null == value) {
            return null;
        }

        if (List.class.isAssignableFrom(value.getClass())) {
            return (List) value;
        }

        if (Array.class.isAssignableFrom(value.getClass())) {
            return Arrays.asList(toArray(value));
        }

        if (Collection.class.isAssignableFrom(value.getClass())) {
            return new ArrayList((Collection) value);
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> Set<T> toSet(Object value) {
        if (null == value) {
            return null;
        }

        if (Set.class.isAssignableFrom(value.getClass())) {
            return (Set) value;
        }

        if (Array.class.isAssignableFrom(value.getClass())) {
            return new HashSet(Arrays.asList(toArray(value)));
        }

        if (Collection.class.isAssignableFrom(value.getClass())) {
            return new HashSet<>((Collection) value);
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> Queue<T> toQueue(Object value) {
        if (null == value) {
            return null;
        }

        if (Queue.class.isAssignableFrom(value.getClass())) {
            return (Queue) value;
        }

        if (Array.class.isAssignableFrom(value.getClass())) {
            return new ArrayDeque(Arrays.asList(toArray(value)));
        }

        if (Collection.class.isAssignableFrom(value.getClass())) {
            return new ArrayDeque<>((Collection) value);
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> Collection<T> toCollection(Object value) {
        if (null == value) {
            return null;
        }

        if (Collection.class.isAssignableFrom(value.getClass())) {
            return (Collection) value;
        }
        return null;
    }

    @SuppressWarnings({"unchecked"})
    public <K, V> Map<K, V> toMap(Object value) {
        if (null == value) {
            return null;
        }

        if (value instanceof Map) {
            return (Map<K, V>) value;
        }
        return null;
    }

    /**
     * 将对象按照目标类型进行转换
     *
     * @param tClass 要转出的类型
     * @param value  待转换的值
     * @param <T>    类型
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    public <T> T cast(Class<? extends T> tClass, Object value) {
        if (null == tClass || null == value) {
            return null;
        }

        // 对于是对应的实例，则直接转换，或者要抓换的是其父类，则也可以直接转换
        if (tClass.isInstance(value) || tClass.isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        if (tClass == char.class || tClass == Character.class) {
            return (T) toChar(value);
        }
        if (tClass == String.class) {
            return (T) toStr(value);
        }

        if (tClass == byte.class || tClass == Byte.class) {
            return (T) toByte(value);
        }

        if (tClass == short.class || tClass == Short.class) {
            return (T) toShort(value);
        }

        if (tClass == int.class || tClass == Integer.class) {
            return (T) toInt(value);
        }

        if (tClass == long.class || tClass == Long.class) {
            return (T) toLong(value);
        }

        if (tClass == float.class || tClass == Float.class) {
            return (T) toFloat(value);
        }

        if (tClass == double.class || tClass == Double.class) {
            return (T) toDouble(value);
        }

        if (tClass.isEnum()) {
            return toEnum(tClass, value);
        }

        if (tClass.isArray() || Array.class.isAssignableFrom(tClass)) {
            return (T) toArray(value);
        }

        if (List.class.isAssignableFrom(tClass)) {
            return (T) toList(value);
        }

        if (Set.class.isAssignableFrom(tClass)) {
            return (T) toSet(value);
        }

        if (Queue.class.isAssignableFrom(tClass)) {
            return (T) toQueue(value);
        }

        if (Collection.class.isAssignableFrom(tClass)) {
            return (T) toCollection(value);
        }

        // 如果是基本类型，则按照基本类型处理
        if (value.getClass().isPrimitive()) {
            return castStr(tClass, String.valueOf(value));
        }

        if (value instanceof String) {
            return JSON.parseObject((String) value, tClass);
        }

        throw new RuntimeException("值 " + value + " 向类型 " + tClass.getName() + " 转换异常");
    }

    /**
     * 将对象的数据，按照cls类型进行转换
     *
     * @param cls  待转换的Class类型
     * @param data 数据
     * @param <T>  返回类型
     * @return Class类型对应的对象
     */
    public <T> T castStr(Class<T> cls, String data) {
        if (cls.equals(String.class)) {
            // 针对data为null的情况进行转换
            if (NULL_STR.equals(data)) {
                return null;
            }
            return cls.cast(data);
        } else if (Character.class.isAssignableFrom(cls)) {
            return cls.cast(data.toCharArray());
        } else {
            try {
                if (NULL_STR.equals(data)) {
                    return null;
                }
                return cls.cast(cls.getMethod("valueOf", String.class).invoke(null, data));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error(LOG_PRE + "castStr error", e);
            }
            return null;
        }
    }

    /**
     * 对象是否为空
     * <p>
     * 种情况：
     * <ul>
     *     <li>1.为null，则为空</li>
     *     <li>2.为字符类型，则如果为空字符，则为空</li>
     *     <li>3.为集合类型，则如果集合个数为空，则为空</li>
     * </ul>
     *
     * @param object 待核查对象
     * @return true:为空，false:不空
     */
    @SuppressWarnings("rawtypes")
    public Boolean isEmpty(Object object) {
        if (null == object) {
            return true;
        }
        if (object instanceof String) {
            String valueStr = (String) object;
            return "".equals(valueStr);
        }
        if (object instanceof Collection) {
            Collection collection = (Collection) object;
            return collection.isEmpty();
        }
        return false;
    }

    public Boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }
}
