package com.simon.simba.codegen;

import com.simon.neo.Neo;
import com.simon.neo.db.NeoColumn;
import com.simon.simba.entity.ShowFieldInfo;
import freemarker.template.TemplateException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Setter;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

/**
 * 前端生成器
 * @author zhouzhenyong
 * @since 2019/1/2 下午3:17
 */
@Setter
@SuppressWarnings("unchecked")
public class FrontCodeGen {
    /**
     * 应用名字，用于前端创建目录用，建议用一个小写的单词
     */
    private String appName;
    /**
     * mysql的时间字段
     */
    private static final List<String> mysqlTimeType = Arrays.asList("DATETIME", "TIMESTAMP");

    /**
     * mysql的枚举类型
     */
    private static final String mysqlEnumType = "ENUM";

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
    private String dbUrl;
    private String dbUserName;
    private String dbUserPassword;

    /**
     * 后端的端口
     */
    private String backendPort;
    /**
     * 后端的url
     */
    private String backendUrl;

    /**
     * 用于表名和中文映射
     */
    private Map<String, String> tableNameMap = new HashMap<>();
    /**
     * 搜索框搜索的字段
     */
    private Map<String, List<String>> searchFieldsMap = new HashMap<>();
    /**
     * 表格展示的字段
     */
    private Map<String, List<ShowFieldInfo>> tableShowFieldsMap = new HashMap<>();
    /**
     * 不展示的字段
     */
    private Map<String, List<String>> excludesFieldsMap = new HashMap<>();
    /**
     * 更新的字段
     */
    private Map<String, List<String>> canUpdateFieldsMap = new HashMap<>();
    /**
     * 不需要更新的字段，在更新框中是禁用状态
     */
    private Map<String, List<String>> cantUpdateFieldsMap = new HashMap<>();
    /**
     * 在字段更新或者添加的时候，字段是否必需
     */
    private Map<String, List<String>> unRequiredFieldsMap = new HashMap<>();
    /**
     * 在字段更新或者添加的时候，字段是否必需
     */
    private Map<String, List<String>> requiredFieldsMap = new HashMap<>();
    /**
     * 属性名和界面展示的中文映射，如果没有指定，则用数据库的注释
     */
    private Map<String, Map<String, String>> tableFieldNameMap = new HashMap<>();
    /**
     * 表的属性为时间类型的字段
     */
    private Map<String, List<String>> tableTimeFieldMap = new HashMap<>();
    /**
     * 表中那些字段是图片，用于在前端进行图片展示
     */
    private Map<String, List<String>> tablePicFieldMap = new HashMap<>();

    public void setIncludes(String ...tables){
        includeTables.addAll(Arrays.asList(tables));
    }

    public void setExcludes(String ...tables){
        excludeTables.addAll(Arrays.asList(tables));
    }

    /**
     * 搜索字段不宜太多
     */
    public void setSearchFieldsMap(Map<String, List<String>> fieldMap){
        if(null == fieldMap || fieldMap.size() == 0){
            return;
        }

        searchFieldsMap.putAll(fieldMap);
    }

    public void setTableShowFieldsMap(Map<String, List<ShowFieldInfo>> fieldMap){
        if(null == fieldMap || fieldMap.size() == 0){
            return;
        }
        tableShowFieldsMap.putAll(fieldMap);
    }

    private String getFieldDesc(String tableName, String fieldName) {
        if (null != tableName && !"".equals(tableName) && null != fieldName && !"".equals(fieldName)) {
            if (tableFieldNameMap.containsKey(tableName)) {
                return tableFieldNameMap.get(tableName).get(fieldName);
            }
        }
        return "";
    }

    private void setDataMapBaseInfo(Map<String, Object> dataMap, String tableNameAfterPre){
        dataMap.put("tablePathName", getTablePathName(tableNameAfterPre));
        dataMap.put("tableUrlName", getTableUrlName(tableNameAfterPre));
        dataMap.put("tablePathNameLower", getTablePathNameLower(tableNameAfterPre));

        dataMap.put("tablePathSplitLower", getTablePathSplitLower(tableNameAfterPre));
    }

    private void configDBInfo(Map<String, Object> dataMap, String tableName){
        Neo neo = Neo.connect(dbUrl, dbUserName, dbUserPassword);
        List<NeoColumn> columns = neo.getColumnList(tableName);

        //****** 设置表基本信息 ******
        configTableName(dataMap, tableName);
        configEnumTypeField(dataMap, columns);
        configTimeField(columns, tableName);
        configFieldName(columns, tableName);

        //****** 设置表搜索框的配置 ******
        configSearchField(dataMap, columns, tableName);

        //****** 设置表界面展示信息配置 ******
        configTableShowField(dataMap, tableName);

        //****** 设置表界面每一行展开的信息配置 ******
        configExpandField(dataMap, columns, tableName);

        //****** 设置表新增的配置 ******
        configAddField(dataMap, columns, tableName);

        //****** 设置数据更新的配置 ******
        configUpdateField(dataMap, columns, tableName);
    }

    private void configBackend(Map<String, Object> dataMap){
        dataMap.put("backendPort", backendPort);
        dataMap.put("backendUrl", backendUrl);
    }

    /**
     * 将表中文名和表名对应起来
     */
    private void configTableName(Map<String, Object> dataMap, String tableName){
        dataMap.put("tableNameCn", tableNameMap.get(tableName));
    }

    /**
     * 添加枚举类型和对应的值
     */
    private void configEnumTypeField(Map<String, Object> dataMap, List<Column> columns){
        List<EnumInfo> infoList = new ArrayList<>();
        if (null != columns && !columns.isEmpty()) {
            columns.stream().filter(c->c.getType().equals(mysqlEnumType))
                .forEach(c-> infoList.add(EnumInfo.of(c.getName(), getEnumValueList(c.getRemarks()))));
        }
        dataMap.put("enumFields", infoList);
    }

    /**
     * 根据时间字段表，将各个表中的时间字段在界面上进行转换
     */
    private void configTimeField(List<Column> columns, String tableName){
        if(null != columns && !columns.isEmpty()){
            columns.forEach(c->{
                if(mysqlTimeType.contains(c.getType())) {
                    tableTimeFieldMap.compute(tableName, (k, v) -> {
                        if (null == v) {
                            List<String> dataList = new ArrayList<>();
                            dataList.add(c.getName());
                            return dataList;
                        } else {
                            v.add(c.getName());
                            return v;
                        }
                    });
                }
            });
        }
    }

    /**
     * 设置表的属性名和名称的对应，如果没有设置，则用DB中的注释，如果注释也没有，则直接用name
     */
    private void configFieldName(List<Column> columns, String tableName){
        if(null != tableName && !"".equals(tableName)){
            Map<String, String> fieldMap = tableFieldNameMap.get(tableName);
            if (null != fieldMap) {
                if (null != columns && !columns.isEmpty()) {
                    columns.forEach(c -> {
                        fieldMap.compute(c.getName(), (k, v) -> {
                            if (null == v) {
                                String remarks = c.getRemarks();
                                if (null != remarks) {
                                    if (c.getType().equals(mysqlEnumType)) {
                                        return getEnumDesc(remarks);
                                    }
                                    return remarks;
                                }
                                return c.getName();
                            } else {
                                return v;
                            }
                        });
                    });
                }
            }
        }
    }

    /**
     * 设置搜索字段
     */
    private void configSearchField(Map<String, Object> dataMap, List<Column> columns, String tableName){
        List<FieldInfo> searchFieldMapList = new ArrayList<>();
        Optional.ofNullable(searchFieldsMap).map(searchField -> {
            Optional.ofNullable(searchFieldsMap.get(tableName)).map(fields -> {
                fields.forEach(f -> {
                    if (null != columns && !columns.isEmpty()) {
                        if (columns.stream().anyMatch(c -> c.getName().equals(f))) {
                            FieldInfo info = FieldInfo.of(f, getFieldDesc(tableName, f));

                            // 时间戳设置
                            if (fieldIsTimeField(tableName, info.getName())){
                                info.setTimeFlag(1);
                            }

                            // 枚举类型设置
                            if (fieldIsEnum(columns, f)){
                                info.setEnumFlag(1);
                            }

                            searchFieldMapList.add(info);
                        }
                    }
                });
                return null;
            });
            return null;
        });

        dataMap.put("searchFields", searchFieldMapList);
    }

    /**
     * 对于没有设置描述字段的，则设置数据库的描述字段
     */
    private void configTableShowField(Map<String, Object> dataMap, String tableName){
        List<ShowFieldInfo> searchFieldMapList = new ArrayList<>();
        Optional.ofNullable(tableShowFieldsMap).map(tableShowFieldMap -> {
            Optional.ofNullable(tableShowFieldMap.get(tableName)).map(fields->{
                fields.forEach(f -> {
                    FieldInfo fieldInfo = f.getFieldInfo();
                    try {
                        fieldInfo.setDesc(getFieldDesc(tableName, fieldInfo.getName()));

                        // 时间字段转换
                        if (fieldIsTimeField(tableName, fieldInfo.getName())) {
                            fieldInfo.setTimeFlag(1);
                        }

                        // 图片类型转换
                        if (fieldIsPicField(tableName, fieldInfo.getName())) {
                            fieldInfo.setPicFlag(1);
                        }

                        searchFieldMapList.add(f);
                    } catch (NullPointerException e) {
                        System.out.println("fieldName = " + fieldInfo.getName());
                        e.printStackTrace();
                    }
                });
                return null;
            });
            return null;
        });
        dataMap.put("tableShowFields", searchFieldMapList);
    }

    /**
     * 判断表的某个属性是否为时间类型
     */
    private boolean fieldIsTimeField(String tableName, String field){
        if (null != tableTimeFieldMap) {
            List<String> fields = tableTimeFieldMap.get(tableName);
            if(null != fields && !fields.isEmpty()){
                return fields.contains(field);
            }
        }
        return false;
    }

    /**
     * 判断表的某个属性是否为必需填写，用于添加框和编辑框中的数据必填设置
     */
    private boolean fieldIsRequired(String tableName, String field) {
        List<String> fieldList = unRequiredFieldsMap.get(tableName);
        // 1.（必需名单不空且包含）则不可更新
        if (null != fieldList && !fieldList.isEmpty() && fieldList.contains(field)) {
            return false;
        }

        // 2.（非必需名单不空且不包含）则不放过
        fieldList = requiredFieldsMap.get(tableName);
        if (null != fieldList && !fieldList.isEmpty() && !fieldList.contains(field)) {
            return false;
        }

        return true;
    }

    /**
     * 判断表的某个属性是否可以给更新，如果不允许，则编辑框中该字段灰色
     */
    private boolean fieldCanEdit(String tableName, String fieldName){
        List<String> fieldList = cantUpdateFieldsMap.get(tableName);
        // 1.（不能更新名单不空且包含）则不可更新
        if (null != fieldList && !fieldList.isEmpty() && fieldList.contains(fieldName)) {
            if (fieldList.contains(fieldName)) {
                return false;
            }
        }

        // 2.（可更新名单不空且不包含）则不放过
        fieldList = canUpdateFieldsMap.get(tableName);
        if (null != fieldList && !fieldList.isEmpty() && !fieldList.contains(fieldName)) {
            return false;
        }
        return true;
    }

    /**
     * 判断表的某个属性是否为时间类型
     */
    private boolean fieldIsPicField(String tableName, String field) {
        List<String> fields = tablePicFieldMap.get(tableName);
        if (null != fields && !fields.isEmpty()) {
            return fields.contains(field);
        }
        return false;
    }

    /**
     * 判断字段是否为枚举类型
     */
    private boolean fieldIsEnum(List<Column> columns, String field) {
        if (null != columns && !columns.isEmpty()) {
            return columns.stream().anyMatch(c -> c.getName().equals(field) && c.getType().equals(mysqlEnumType));
        }
        return false;
    }

    /**
     * 对于枚举类型，获取其中枚举类型的描述
     * @param fieldDesc 性别用户的性别:MALE=男性;FEMALE=女性;UNKNOWN=未知
     * @return 性别用户的性别
     */
    private String getEnumDesc(String fieldDesc){
        if (null == fieldDesc) {
            return null;
        }

        return getEnumDesc(fieldDesc, Arrays.asList(":", "：", ",", "，"));
    }

    private String getEnumDesc(String fieldDesc, List<String> splitStrs){
        for (String splitStr : splitStrs) {
            if(fieldDesc.contains(splitStr)) {
                return Arrays.asList(fieldDesc.split(splitStr)).get(0);
            }
        }
        return fieldDesc;
    }

    /**
     * 获取枚举值的key和value
     * @param str 比如：性别用户的性别:MALE=男性;FEMALE=女性;UNKNOWN=未知
     * @return {MALE:男性, FEMAIL=女性, UNKNOWN=未知}
     */
    private List<EnumMeta> getEnumValueList(String str){
        if (null == str) {
            return null;
        }

        return getEnumValueList(str, Arrays.asList(";", ",", "，"));
    }

    private List<EnumMeta> getEnumValueList(String str, List<String> splitStrs){
        for (String splitStr : splitStrs) {
            if (str.contains(splitStr)) {
                return getEnumValueListFromSemi(str, splitStr);
            }
        }
        return Collections.emptyList();
    }

    /**
     * 分号
     */
    private List<EnumMeta> getEnumValueListFromSemi(String string, String splitStr){
        List<EnumMeta> metaLis = new ArrayList<>();
        List<String> valueList = Arrays.asList(string.split(splitStr));
        if (!valueList.isEmpty()){
            valueList.forEach(v->{
                String key = getKey(v);
                String value = getValue(v);
                if (null != key){
                    if (null != value) {
                        metaLis.add(EnumMeta.of(key, value));
                    } else {
                        metaLis.add(EnumMeta.of(key, key));
                    }
                }
            });
        }
        return metaLis;
    }


    /**
     * = 好前面的，逗号或者分号之间的字符
     */
    private String getKey(String value){
        Integer endIndex = value.indexOf("=");
        return getKeyFromSplit(value, Arrays.asList(":", "：", ",", "，"), endIndex);
    }

    private String getKeyFromSplit(String value, List<String> splitStrs, Integer endIndex){
        for (String splitStr : splitStrs) {
            Integer index = value.indexOf(splitStr);
            if(-1 != index && -1 != endIndex){
                return value.substring(index + 1, endIndex);
            }
        }
        if (-1 != endIndex) {
            return value.substring(0, endIndex);
        }
        return null;
    }

    /**
     * 等号后面的字符
     */
    private String getValue(String value){
        Integer index = value.indexOf("=");
        if(-1 != index){
            return value.substring(index + 1);
        }
        return null;
    }

    /**
     * 设置表的每一行展开字段，排除表格的字段，排除不展示的字段，其他的字段都进行展示
     */
    private void configExpandField(Map<String, Object> dataMap, List<Column> columns, String tableName){
        List<FieldInfo> expandFieldList = new ArrayList<>();
        List<String> tableShowFieldList;
        List<String> excludeFieldList;

        if (null != tableShowFieldsMap && !tableShowFieldsMap.isEmpty()){
            tableShowFieldList = Optional.ofNullable(tableShowFieldsMap.get(tableName))
                .map(f-> f.stream().map(fi->fi.getFieldInfo().getName()).collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
            excludeFieldList = Optional.ofNullable(excludesFieldsMap.get(tableName)).orElseGet(ArrayList::new);

            if (null != columns && !columns.isEmpty()) {
                columns.stream()
                    .filter(c -> !tableShowFieldList.contains(c.getName()))
                    .filter(c -> !excludeFieldList.contains(c.getName()))
                    .forEach(c -> {
                        FieldInfo fieldInfo = FieldInfo.of(c.getName(), getFieldDesc(tableName, c.getName()));

                        // 设置时间字段
                        if (fieldIsTimeField(tableName, c.getName())){
                            fieldInfo.setTimeFlag(1);
                        }

                        // 设置图片字段
                        if (fieldIsPicField(tableName, c.getName())){
                            fieldInfo.setPicFlag(1);
                        }
                        expandFieldList.add(fieldInfo);
                    });
            }
        }

        dataMap.put("expandFields", ListUtils.split(expandFieldList, 4));
    }

    /**
     * 添加弹窗的时候要显示的字段
     * 其中有些这么几个基本字段是不要在添加框中展示的
     */
    private void configAddField(Map<String, Object> dataMap, List<Column> columns, String tableName){
        List<UpdateAddFieldInfo> showFieldInfos = Optional.ofNullable(columns).map(c->
            c.stream()
                .filter(column->!column.getName().equals("id"))
                .filter(column->!column.getName().equals("create_time"))
                .filter(column->!column.getName().equals("update_time"))
                .map(column-> {
                    String fieldName = column.getName();
                    UpdateAddFieldInfo info = UpdateAddFieldInfo.of(fieldName, getFieldDesc(tableName, fieldName));

                    // 设置哪些字段是不必需要填写的
                    if(fieldIsRequired(tableName, column.getName())){
                        info.setRequire(1);
                    }

                    // 设置时间类型
                    if (fieldIsTimeField(tableName, fieldName)){
                        info.getFieldInfo().setTimeFlag(1);
                    }

                    // 设置枚举类型
                    if (column.getType().equals(mysqlEnumType)){
                        info.getFieldInfo().setEnumFlag(1);
                    }
                    return info;
                })
                .collect(Collectors.toList())).orElseGet(ArrayList::new);
        dataMap.put("dataAddFields", showFieldInfos);
    }

    /**
     * 设置哪些字段是可以更新的，首先过滤排除表，然后查看展示表
     */
    private void configUpdateField(Map<String, Object> dataMap, List<Column> columns, String tableName) {
        List<UpdateAddFieldInfo> fieldInfos = new ArrayList<>();
        if (null != columns && !columns.isEmpty()) {
            fieldInfos = columns.stream().map(c -> {
                UpdateAddFieldInfo info = UpdateAddFieldInfo.of(c.getName(), getFieldDesc(tableName, c.getName())).setCanEdit(1);
                // 设置哪些是需要更新的
                if(!fieldCanEdit(tableName, c.getName())){
                    info.setCanEdit(0);
                }

                // 设置哪些字段是不必需要填写的
                if(fieldIsRequired(tableName, c.getName())){
                    info.setRequire(1);
                }

                // 设置时间类型
                if (fieldIsTimeField(tableName, c.getName())){
                    info.getFieldInfo().setTimeFlag(1);
                }

                // 设置枚举类型
                if (c.getType().equals(mysqlEnumType)){
                    info.getFieldInfo().setEnumFlag(1);
                }

                return info;
            }).collect(Collectors.toList());
        }
        dataMap.put("updateFields", fieldInfos);
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
     * config_group -> configgroup
     */
    private String getTablePathSplitLower(String tableName){
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

    /**
     * 菜单的路径配置文件
     */
    private void writeRouterConfig(Map<String, Object> dataMap, String filePath){
        try {
            String oldRouterConfigText = FileUtil.read(filePath);
            String dbName = String.valueOf(dataMap.get("dbName"));
            List<Map<String, String>> componentInfos = (List<Map<String, String>>) dataMap.get("tableComponentInfos");

            if (null != componentInfos && !componentInfos.isEmpty()) {
                List<Triple> tripleList = componentInfos.stream()
                    .map(c -> new MutableTriple<>(dbName, c.get("tableName"), c.get("tablePathName")))
                    .collect(Collectors.toList());

                FileUtil.write(filePath, addConfigRouter(oldRouterConfigText, tripleList));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 菜单的路径配置文件
     */
    private void writeMenu(Map<String, Object> dataMap, String filePath) {
        try {
            String oldMenuText = FileUtil.read(filePath);
            List<TableInfo> tableInfoList = (List<TableInfo>) dataMap.get("tableInfos");

            if (null != tableInfoList && !tableInfoList.isEmpty()) {
                List<Pair> pairList = tableInfoList.stream().map(t -> new MutablePair<>(t.getName(), t.getDesc()))
                    .collect(Collectors.toList());

                FileUtil.write(filePath, addMenu(oldMenuText, pairList));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向文件中后面添加菜单
     *
     * @param oldMenuText 菜单源码中的旧数据
     * @param tableInfoList 表的信息：tableName 表名，比如：talklist；其中单词之间都是小写拼接，只是一种默认；tableDesc 表的描述，比如：说说列表
     * @return 返回加入后的菜单数据
     */
    private String addMenu(String oldMenuText, List<Pair> tableInfoList) {
        String endStr = "};";
        Integer inputStartIndex = oldMenuText.indexOf(endStr);
        if (-1 != inputStartIndex) {
            StringBuilder sb = new StringBuilder(oldMenuText.substring(0, inputStartIndex));
            if(null != tableInfoList && !tableInfoList.isEmpty()) {
                tableInfoList.forEach(d -> {
                    StringBuilder tem = new StringBuilder();
                    tem.append("  'menu.").append(d.getKey()).append("List': '").append(d.getValue()).append("',\n");

                    // 如果已经存在则不添加
                    if (!oldMenuText.contains(tem.toString())) {
                        sb.append(tem.toString());
                    }
                });
            }
            sb.append(endStr);
            return sb.toString();
        }
        return oldMenuText;
    }

    /**
     * 新增菜单的路由
     *
     * @param oldRouterConfigText 旧的配置文件路径
     * @param dbTableInfoList: 里面包括：db 库名字，用于路径; tableName 表名，两个小写拼接; tablePathName 表路径名
     * @return 新增菜单后的路由菜单
     */
    private String addConfigRouter(String oldRouterConfigText, List<Triple> dbTableInfoList) {
        String endStr = ""
            + "      {\n"
            + "        component: '404',\n"
            + "      },\n"
            + "    ],\n"
            + "  },\n"
            + "];";
        Integer inputStartIndex = oldRouterConfigText.indexOf(endStr);
        if (-1 != inputStartIndex) {
            StringBuilder sb = new StringBuilder(oldRouterConfigText.substring(0, inputStartIndex));
            if(null != dbTableInfoList && !dbTableInfoList.isEmpty()) {
                dbTableInfoList.forEach(d -> {
                    StringBuilder tem = new StringBuilder();
                    tem.append("      {\n")
                        .append("        path: '/").append(d.getMiddle()).append("',\n")
                        .append("        name: '").append(d.getMiddle()).append("List',\n")
                        .append("        component: './").append(d.getLeft()).append("/").append(d.getRight()).append("List',\n")
                        .append("      },\n");

                    // 如果已经存在则不添加
                    if (!oldRouterConfigText.contains(tem.toString())) {
                        sb.append(tem.toString());
                    }
                });
            }
            sb.append(endStr);
            return sb.toString();
        }
        return oldRouterConfigText;
    }

    private void writeFile(Map<String, Object> dataMap, String filePath, String templateName){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FileUtil.getFile(filePath)));
            Objects.requireNonNull(FreeMarkerTemplateUtil.getTemplate(templateName)).process(dataMap, bufferedWriter);
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
    }

    private void configTableMenu(Map<String, Object> dataMap){
        dataMap.put("tableInfos", tableNameMap.entrySet().stream()
            .map(e -> TableInfo.of(getTablePathSplitLower(excludePreFix(e.getKey())), e.getValue()))
            .collect(Collectors.toList()));

        dataMap.put("tableComponentInfos", tableNameMap.entrySet().stream()
            .map(e-> {
                String tableNameAfterPre = excludePreFix(e.getKey());
                return Maps.of()
                    .add("tableName", getTablePathSplitLower(tableNameAfterPre))
                    .add("tablePathName", getTablePathName(tableNameAfterPre))
                    .build();
            }).collect(Collectors.toList()));
    }

    private Map<String, Object> generateBone(){
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("backendPort", backendPort);
        dataMap.put("backendUrl", backendUrl);
        dataMap.put("dbName", appName);

        // 设置后端信息（端口和url）
        configBackend(dataMap);

        // 配置所有表和表名的对应
        configTableMenu(dataMap);

        // config.js
        writeFile(dataMap, codePath + "/config/config.js", "frontConfig.ftl");

        // router.config.js
        writeRouterConfig(dataMap, codePath + "/config/router.config.js");

        // menu.js
        writeMenu(dataMap, codePath + "/src/locales/zh-CN/menu.js");

        // api.js 用户登录信息
        writeFile(dataMap, codePath + "/src/services/api.js", "loginApi.ftl");

        return dataMap;
    }

    public void generate(){
        Map<String, Object> dataMap = generateBone();
        if(null != includeTables && includeTables.size() > 0){
            includeTables.stream().filter(t->!excludeTables.contains(t)).forEach(t->{
                dataMap.put("tableName", t);

                String tableNameAfterPre = excludePreFix(t);
                setDataMapBaseInfo(dataMap, tableNameAfterPre);
                configDBInfo(dataMap, t);

                // List
                writeFile(dataMap, codePath + "/src/pages/"+appName+"/" + getTablePathName(tableNameAfterPre) + "List.js", "tableList.ftl");
                writeFile(dataMap, codePath + "/src/pages/"+appName+"/" + getTablePathName(tableNameAfterPre) + "List.less", "tableList.less");
                // model
                writeFile(dataMap, codePath + "/src/models/" + getTablePathNameLower(tableNameAfterPre) + "Model.js", "tableModel.ftl");
                // api
                writeFile(dataMap, codePath + "/src/services/" + getTablePathNameLower(tableNameAfterPre) + "Api.js", "tableApi.ftl");
            });
        }
    }

    public static void main(String ...args){
        FrontCodeGen front = new FrontCodeGen();

        // 设置代码路径
        front.setCodePath("/Users/zhouzhenyong/work/likekj/like-base-front");

        /**************************************** 后端配置（必填） ****************************************/
        // 设置要监听的后端端口号
        front.setBackendPort("8087");
        // 设置跟后端交互的url，最后一定不要带/
        front.setBackendUrl("/top/admin/api/v1");

        /**************************************** DB配置（必填） ****************************************/
        // 设置数据库信息
        front.setDbUrl("jdbc:mysql://118.31.38.50:3306/like?useUnicode=true&characterEncoding=UTF-8");
        front.setAppName("like");
        front.setDbUserName("like");
        front.setDbUserPassword("Like@123");

        /**************************************** 要展示的表基本信息（必填） **********************/
        // 设置表前缀过滤
        front.setPreFix("tp_");
        // 设置要输出的表
        front.setIncludes("lk_talk", "lk_user");
        // 设置要排除的表
//        front.setExcludes("lk_user");

        // 表和表的中文名对应
        front.setTableNameMap(
            Maps.of()
                .put("lk_talk", "说说")
                .add("lk_user", "用户")
                .build()
        );

        /***************************************** 表的属性信息（非必填） *********************************/
        // 设置表的属性和界面中文展示，主要用于中文关联，如果不设置，则默认用数据库的注释
        front.setTableFieldNameMap(
            Maps.of()
                .add("lk_talk", Maps.of()
                    .add("id", "id").add("title", "标题").add("status", "状态").add("address", "地址")
                    .build())
                .add("lk_user", Maps.of()
                    .add("id", "id").add("name", "姓名").add("head_img", "头像")
                    .build())
                .build()
        );

        // 设置表中的某些字段界面显示为图片
        front.setTablePicFieldMap(
            Maps.of()
                .add("lk_talk", Arrays.asList("head_img"))
                .add("lk_user", Arrays.asList("head_img"))
                .build()
        );

        /********************************************* 界面属性（查看具体） **************************************************/
        // 不展示字段：一旦设置界面上就不会展示（可不填）
        front.setExcludesFieldsMap(
            Maps.of()
                //.add("lk_talk", Arrays.asList("update_time"))
                .add("lk_user", Arrays.asList("update_by"))
                .build()
        );

        // 搜索字段：用于界面展示那些搜索框（必填）
        front.setSearchFieldsMap(
            Maps.of()
                .add("tp_invite_relate", Arrays.asList("id", "invite_key", "create_time"))
                .add("tp_marriage", Arrays.asList("id", "male_name", "female_name", "male_lunar_flag"))
                .build()
        );

        // 表格展示字段：字段（必填）和占的大小（每个表最大可能85，大小可以不设置，默认20）
        front.setTableShowFieldsMap(
            Maps.of()
                .add("tp_invite_relate", Arrays.asList(
                    ShowFieldInfo.of("id"),
                    ShowFieldInfo.of("invite_key"),
                    ShowFieldInfo.of("create_time"),
                    ShowFieldInfo.of("update_time")
                ))
                .add("tp_marriage", Arrays.asList(
                    ShowFieldInfo.of("id"),
                    ShowFieldInfo.of("user_id"),
                    ShowFieldInfo.of("male_name").setRate(10),
                    ShowFieldInfo.of("female_name").setRate(10),
                    ShowFieldInfo.of("male_birthday").setRate(18),
                    ShowFieldInfo.of("female_lunar_flag").setRate(18),
                    ShowFieldInfo.of("create_time").setRate(18)
                ))
                .build()
        );

        /***************** 更新属性字段（非必填） *******************/
        // 启用字段：跟下面属性相反，用于启用字段较少情况
//        front.setCantUpdateFieldsMap(
//            Maps.of()
//                .add("lk_talk", Arrays.asList("create_by"))
//                .add("lk_user", Arrays.asList("create_time"))
//                .build()
//        );

        // 禁用字段：用于更新的弹窗中属性的禁用，跟上面属性设置相反
        front.setCantUpdateFieldsMap(
            Maps.of()
                .add("lk_talk", Arrays.asList("id", "create_by", "create_time", "update_time"))
                .add("lk_user", Arrays.asList("id", "create_time", "update_time"))
                .build()
        );

        // 字段是否必填：设置哪些字段是在增加和更新时候是必需的
        front.setRequiredFieldsMap(
            Maps.of()
                .add("lk_talk", Arrays.asList("id"))
                .add("lk_user", Arrays.asList("id"))
                .build()
        );

        // generate
        front.generate();
    }
}
