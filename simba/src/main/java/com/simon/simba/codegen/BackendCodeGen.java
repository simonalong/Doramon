package com.simon.simba.codegen;

import com.simon.simba.FileUtil;
import com.simon.simba.FreeMarkerTemplateUtil;
import com.simon.simba.Strings;
import freemarker.template.TemplateException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 后端控制台生成器
 * @author zhouzhenyong
 * @since 2019/1/2 下午3:18
 */
@Setter
@Accessors(chain = true)
public class BackendCodeGen {
    /**
     * 库名字
     */
    private String dbName;
    /**
     * 应用名字
     */
    private String appName;
    /**
     * 应用的包路径
     */
    private String appPath;
    /**
     * 代码生成路径
     */
    private String codePath;
    /**
     * 表名前缀
     */
    private String preFix;
    /**
     * 待生成的表
     */
    private List<String> includeTables = new ArrayList<>();
    /**
     * 不需要生成的表
     */
    private List<String> excludeTables = new ArrayList<>();

    public BackendCodeGen setIncludes(String ...tables){
        includeTables.addAll(Arrays.asList(tables));
        return this;
    }

    public BackendCodeGen setExcludes(String ...tables){
        excludeTables.addAll(Arrays.asList(tables));
        return this;
    }

    public BackendCodeGen setAppPath(String appPath){
        this.appPath = appPath;
        this.codePath += appPath.replace(".", "/") + "/";
        return this;
    }

    public BackendCodeGen setCodePath(String codePath){
        if(codePath.endsWith("/")){
            this.codePath = codePath + "src/main/java/";
        }else{
            this.codePath = codePath + "/src/main/java/";
        }
        return this;
    }

    public void generate(){
        Map<String, Object> dataMap = generateBone();
        if(null != includeTables && includeTables.size() > 0){
            includeTables.stream().filter(t->!excludeTables.contains(t)).forEach(t->{
                dataMap.put("tableName", t);

                String tableNameAfterPre = excludePreFix(t);
                dataMap.put("tablePathName", getTablePathName(tableNameAfterPre));
                dataMap.put("tableUrlName", getTableUrlName(tableNameAfterPre));
                dataMap.put("tablePathNameLower", getTablePathNameLower(tableNameAfterPre));
                // controller
                writeFile(dataMap, codePath + "/controller/" + getTablePathName(tableNameAfterPre) + "Controller.java", "controller.ftl");
                // service
                writeFile(dataMap, codePath + "/service/" + getTablePathName(tableNameAfterPre) + "Service.java", "service.ftl");
                // dao
//                writeFile(dataMap, codePath + "/dao/" + getTablePathName(tableNameAfterPre) + "Dao.java", "dao.ftl");
            });
        }

        System.out.println("finish");
    }

    /**
     * 生成后端骨架代码
     */
    private Map<String, Object> generateBone(){
        Map<String, Object> dataMap = new HashMap<>(16);
        dataMap.put("dbName", dbName);
        dataMap.put("appName", appName);
        dataMap.put("appPath", appPath);

        // DbConfiguration
        writeFile(dataMap, codePath + "/config/DbConfiguration.java", "dbConfiguration.ftl");
        // DbProperties
        writeFile(dataMap, codePath + "/config/DbProperties.java", "dbProperty.ftl");

        // AdminConstant
        writeFile(dataMap, codePath + "/constants/AdminConstant.java", "adminConstant.ftl");

        // BaseResponseController
        writeFile(dataMap, codePath + "/controller/BaseResponseController.java", "baseResponseController.ftl");

        // AdminUserController
        writeFile(dataMap, codePath + "/controller/AdminUserController.java", "adminUserController.ftl");
//        // BaseDao
//        writeFile(dataMap, codePath + "/dao/BaseDao.java", "baseDao.ftl");

        // BaseService
        writeFile(dataMap, codePath + "/service/BaseService.java", "baseService.ftl");

//        // TypeUtils
//        writeFile(dataMap, codePath + "/util/TypeUtils.java", "typeUtils.ftl");
//        // RecordUtils
//        writeFile(dataMap, codePath + "/util/RecordUtils.java", "recordUtils.ftl");

        // AccountEntity
        writeFile(dataMap, codePath + "/view/AccountEntity.java", "accountEntity.ftl");

        // LoginResponseEntity
        writeFile(dataMap, codePath + "/view/LoginResponseEntity.java", "loginResponseEntity.ftl");

        // AdminApplication
        writeFile(dataMap, codePath + "/AdminApplication.java", "adminApplication.ftl");
        return dataMap;
    }

    /**
     * config_group -> ConfigGroup
     */
    private String getTablePathName(String tableName){
        return Strings.toCamelCaseAll(tableName);
    }

    /**
     * config_group -> config_group
     */
    private String getTableUrlName(String tableName){
        return tableName;
    }

    /**
     * config_group -> configGroup
     */
    private String getTablePathNameLower(String tableName){
        return Strings.toCamelCaseStrict(tableName);
    }

    /**
     * lk_config_group -> config_group
     */
    private String excludePreFix(String tableName){
        if(tableName.startsWith(preFix)){
            return tableName.substring(preFix.length());
        }
        return tableName;
    }

    private void writeFile(Map<String, Object> dataMap, String filePath, String templateName){
        try {
            if (!new File(filePath).exists()) {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FileUtil.getFile(filePath)));
                Objects.requireNonNull(FreeMarkerTemplateUtil.getTemplate(templateName)).process(dataMap, bufferedWriter);
            }
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String ...args){
        new BackendCodeGen()
        // 设置代码生成的路径
        .setCodePath("/Users/zhouzhenyong/project/private/Doramon/integration")

        // 设置数据库的名字，只是用于数据的标记
        .setDbName("king")
        // 设置应用名字，用于对外的url路径
        .setAppName("simon/king")
        // 设置应用的打包路径
        .setAppPath("com.simon.doraemon")

        // 设置表前缀过滤
        .setPreFix("t_")
        // 设置要输出的表
        .setIncludes("t_config_group")
        // 设置要排除的表
        //.setExcludes("t_test");

        // 代码生成
        .generate();
    }
}
