package com.simon.ocean;

import java.util.Map;
import java.util.Properties;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.yaml.snakeyaml.Yaml;

/**
 * @author zhouzhenyong
 * @since 2019/2/16 下午5:51
 */
@UtilityClass
public class YmlUtil {

    /**
     * 从配置路径文件中读取配置文件
     * @param classConfigPath 配置文件的路径，非绝对路径：比如/property/test.yml
     */
    public Map<String, Object> ymlToMapFromClassPath(String classConfigPath) {
        try {
            YamlMapFactoryBean yml = new YamlMapFactoryBean();
            yml.setResources(new ClassPathResource(classConfigPath));
            return yml.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从配置路径文件中读取配置文件
     * @param classConfigPath 配置文件的路径，非绝对路径：比如/property/test.yml
     */
    public Properties ymlToPropertiesFromClassPath(String classConfigPath) {
        try {
            YamlPropertiesFactoryBean yml = new YamlPropertiesFactoryBean();
            yml.setResources(new ClassPathResource(classConfigPath));
            return yml.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从绝对路径中读取配置文件
     * @param absoluteFilePath 绝对路径：/user/xxx/.../test.yml
     */
    public Map<String, Object> ymlToMapFromAbsolutePath(String absoluteFilePath){
        try {
            YamlMapFactoryBean yml = new YamlMapFactoryBean();
            yml.setResources(new FileSystemResource(absoluteFilePath));
            return yml.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从绝对路径中读取配置文件
     * @param absoluteFilePath 绝对路径：/user/xxx/.../test.yml
     */
    public Properties ymlToPropertiesFromAbsolutePath(String absoluteFilePath){
        try {
            YamlPropertiesFactoryBean yml = new YamlPropertiesFactoryBean();
            yml.setResources(new FileSystemResource(absoluteFilePath));
            return yml.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 直接将yml结构转换为Map结构
     * @param content yml结构的数据
     */
    public Map<String, Object> ymlToMap(String content){
        try {
            Yaml yml = new Yaml();
            return yml.load(content);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 直接将yml结构转换为Map结构
     * @param content yml结构的数据
     */
    public Properties ymlToProperties(String content){
        try {
            YamlPropertiesFactoryBean yml = new YamlPropertiesFactoryBean();
            yml.setResources(new ByteArrayResource(content.getBytes()));
            return yml.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
