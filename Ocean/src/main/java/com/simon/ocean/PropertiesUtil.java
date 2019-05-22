package com.simon.ocean;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * @author zhouzhenyong
 * @since 2019/2/19 上午11:13
 */
@UtilityClass
public class PropertiesUtil {

    /**
     * 从绝对路径中读取配置文件
     * @param absoluteFilePath 绝对路径：/user/xxx/.../test.properties
     */
    public Properties propertiesFromAbsolutePath(String absoluteFilePath){
        try {
            return PropertiesLoaderUtils.loadProperties(new FileSystemResource(absoluteFilePath));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从配置路径文件中读取配置文件
     * @param classConfigPath 配置文件的路径，非绝对路径：比如/property/test.properties
     */
    public Properties propertiesFromClassPath(String classConfigPath) {
        try {
            return PropertiesLoaderUtils.loadProperties(new ClassPathResource(classConfigPath));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据property内容转变为Map结构
     * @param content Map结构对应的数据
     */
    public Map<String, Object> propertyToMap(String content){
        try {
            if(null != content && !"".equals(content)) {
                return propertiesToMap(PropertiesLoaderUtils.loadProperties(new ByteArrayResource(content.getBytes())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从绝对路径中读取配置文件
     * @param absoluteFilePath 绝对路径：/user/xxx/.../test.properties
     */
    public Map<String, Object> propertiesToMapFromAbsolutePath(String absoluteFilePath){
        try {
            return propertiesToMap(PropertiesLoaderUtils.loadProperties(new FileSystemResource(absoluteFilePath)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从配置路径文件中读取配置文件
     * @param classConfigPath 配置文件的路径，非绝对路径：比如/property/test.properties
     */
    public Map<String, Object> propertiesToMapFromClassPath(String classConfigPath) {
        try {
            return propertiesToMap(PropertiesLoaderUtils.loadProperties(new ClassPathResource(classConfigPath)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * properties 转换到map结构
     */
    private Map<String, Object> propertiesToMap(Properties properties){
        if(null != properties){
            return properties.entrySet().stream()
                .collect(Collectors.toMap(d->String.valueOf(d.getKey()), Entry::getValue));
        }
        return null;
    }
}
