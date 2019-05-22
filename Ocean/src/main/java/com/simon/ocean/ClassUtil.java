package com.simon.ocean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 该工具类主要用于反射获取类对应的方法、属性、内部类等，包括公有、私有、受保护的和默认的
 *
 * @author zhouzhenyong
 * @since 2017/6/19.
 */
public class ClassUtil {

    /**
     * 利用递归找一个类的指定方法，如果找不到，去父亲里面找直到最上层Object对象为止。
     *
     * @param clazz 目标类
     * @param methodName 方法名
     * @param classes 方法参数类型数组
     * @return 方法对象
     */
    public static Method getMethod(Class clazz, String methodName, final Class[] classes) throws Exception {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            try {
                method = clazz.getMethod(methodName, classes);
            } catch (NoSuchMethodException ex) {
                if (clazz.getSuperclass() == null) {
                    return method;
                } else {
                    method = getMethod(clazz.getSuperclass(), methodName, classes);
                }
            }
        }
        return method;
    }

    /**
     * @param obj 调整方法的对象
     * @param methodName 方法名
     * @param classes 参数类型数组
     * @param objects 参数数组
     * @return 方法的返回值
     */
    public static Object invoke(final Object obj, final String methodName, final Class[] classes,
        final Object[] objects) {
        try {
            Method method = getMethod(obj.getClass(), methodName, classes);
            method.setAccessible(true);// 调用private方法的关键一句话
            return method.invoke(obj, objects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invoke(final Object obj, final String methodName) {
        return invoke(obj, methodName, new Class[]{}, new Object[]{});
    }

    /**
     * 利用递归查找一个类的所有的属性
     */
    public static Field getField(Class clazz, String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            try {
                field = clazz.getField(fieldName);
            } catch (NoSuchFieldException e1) {
                if (clazz.getSuperclass() == null) {
                    return field;
                } else {
                    field = getField(clazz.getSuperclass(), fieldName);
                }
            }
        }
        return field;
    }

    /**
     * @param obj 要设置数据的对象
     * @param fieldName 数据对象对应的属性名字
     * @param destObject 属性对应的值
     */
    public static void set(final Object obj, final String fieldName, final Object destObject) {
        Field field = getField(obj.getClass(), fieldName);
        field.setAccessible(true);
        try {
            field.set(obj, destObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param obj 获取的数据的对象
     * @param fieldName 数据对象内部的属性的名字
     */
    public static Object get(final Object obj, final String fieldName) {
        Field field = getField(obj.getClass(), fieldName);
        Object data = null;
        try {
            data = field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static Class getClass(Class clazz, String innerClassName) {
        Class<?>[] classes = clazz.getDeclaredClasses();
        boolean findFlag = false;
        for (Class<?> cl : classes) {
            if (innerClassName.equals(cl.getSimpleName())) {
                return cl;
            }
        }
        if (!findFlag) {
            if (clazz.getSuperclass() == null) {
                return null;
            } else {
                return getClass(clazz.getSuperclass(), innerClassName);
            }
        }
        return null;
    }

    /**
     * 获取类内部的子类
     */
    public static Object getClass(Object object, String innerClassName, String fieldName) {
        return null;
    }

    /**
     * 设置类内部的子类数据
     */
    public static Object set() {
        return null;
    }
}

