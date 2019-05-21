package ${appPath}.dao;

import ${appPath}.util.RecordUtils;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import me.zzp.am.Ao;
import me.zzp.am.Column;
import me.zzp.am.Record;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author robot
 */
@Slf4j
@SuppressWarnings("unchecked")
public abstract class BaseDao {

    private static final String LOG_PRE = "baseDao";

    @Autowired
    public Ao ${dbName};

    /**
     * 字段搜索的like前缀
     */
    private static final String LIKE_PRE = "like ";

    /**
     * 比较操作符的前缀
     */
    private static final List<String> thanPre = Arrays.asList(">", "<", ">=", "<=");

    /**
    * mysql的时间类型字符
    */
    private static final List<String> mysqlTimeType = Arrays.asList("datetime", "timestamp");
    private static final List<String> mysqlNumberType = Arrays.asList("tinyint", "smallint", "mediumint", "int",
        "bigint", "float", "double", "real", "decimal");


    private Map<String, List<Column>> tableColumn = new HashMap<>();

    @PostConstruct
    public void init(){
        tableColumn.putIfAbsent(getTableName(), ${dbName}.listColumns(${dbName}.getCatalog(), ${dbName}.getSchema(), getTableName()));
    }

    /**
    * 获取表名
    */
    public abstract String getTableName();

    public Integer insert(Record record){
        record.put("create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        record.put("update_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        ${dbName}.insert(getTableName(), record);
        return 1;
    }

    public Integer delete(Long id){
        return ${dbName}.execute("delete from %s where id = ?", getTableName(), id);
    }

    public Integer update(Record record){
        // 如果前端传递过来的是Long类型，则需要转换
        record.remove("create_time");
        record.put("update_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        ${dbName}.upsert(getTableName(), record);
        return 1;
    }

    public List<Record> getPage(Record record){
        Map<String, Object> fieldMap = getFieldSql(record);
        List<Object> fieldValue = (List<Object>) fieldMap.get("value");
        String sql = String.format("select * from %s %s %s %s", getTableName(),
            fieldMap.get("field"), getOrderBy(record), getLimitStr(record));
        log.info("getPage: sql = {}, param ={}", sql, fieldValue);
        return RecordUtils.typeChg(${dbName}.all(sql, fieldValue.toArray()));
    }

    public Integer count(Record record){
        Map<String, Object> fieldMap = getFieldSql(record);
        List<Object> fieldValue = (List<Object>) fieldMap.get("value");

        String sql = String.format("select count(1) from %s %s", getTableName(), fieldMap.get("field"));
        log.info("count: sql = {}, param ={}", sql, fieldValue);
        return ${dbName}.value(Integer.class, sql, fieldValue.toArray());
    }

    public Record one(Long id){
        if (null != id) {
            return ${dbName}.one("select * from %s where id = ?", getTableName(), id);
        }
        return null;
    }

    /**
    * 根据id列表查询出map集合
    * @param ids id列表
    * @return 根据对应的每个id查询出来的数据
    */
    public Map<Long, Record> getListByIds(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return new HashMap<>();
        }

        // 针对id过长的，这里添加告警
        if(ids.size() > 1000){
            log.warn(LOG_PRE + "in 中的数据过长");
        }

        return shareShop.all("select * from %s where id in (%s)", getTableName(),
            org.apache.commons.lang3.StringUtils.join(ids, ",")).stream()
            .collect(Collectors.toMap(userRecord -> userRecord.getLong("id"), a -> a, (k1, k2) -> k1));
    }

    /**
    * 根据map数据生成field 对应的数据插入sql
    * 排除其中不是字段的类型，比如分页字段
    * @param record 待查询的参数表
    * @return
    * 1.参数名字符合数据库要求
    * 2.输出结果比如：user_id=1231,user_name='xxxx'
    */
    Map<String, Object> getFieldSql(Record record){
        Map<String, Object> result = new HashMap(12);
        List<Object> valueList = new ArrayList<>();
        StringBuilder sb = new StringBuilder("where 1=1 and ");
        boolean haveValue = false;

        // 这里过滤两个常见的处理
        Entry<String, Object>[] entry = record.entrySet().stream().filter(e -> {
            String key = e.getKey();
            if("pager".equals(key)){
                return false;
            }

            if("order by".equals(key)){
                return false;
            }
            return true;
        }).distinct().toArray(Entry[]::new);
        Integer length = entry.length;
        for (int i = 0; i < length; i++) {
            haveValue |= generateMiddleField(entry[i].getKey(), entry[i].getValue(), valueList, sb);
        }

        result.put("field", removeEndAnd(sb.toString()));
        result.put("value", valueList);

        return result;
    }

    /**
     * 排序的设置，如果有设置了，则按照指定的，否则按照更新时间倒序
     */
    public String getOrderBy(Record record) {
        if (record.containsKey("order by")) {
            return "order by " + record.get("order by");
        }
        return "order by update_time desc";
    }

    private String removeEndAnd(String fieldFormat){
        if(fieldFormat.endsWith("and ")){
            Integer index = fieldFormat.lastIndexOf("and ");
            return fieldFormat.substring(0, index);
        }
        return fieldFormat;
    }

    /**
    * 根据特殊场景进行字段的拼接
    * 1.模糊搜索：前端传递过来like_前缀字段，则进行模糊搜索
    * 2.大于> 和小于 <
    * 3.时间戳，前端传递过来的搜索的时间都是range，是一个数组
    * @Return false: 数据为空不满足下面分支；true：满足某个分支
    */
    private boolean generateMiddleField(String field, Object value, List<Object> valueList, StringBuilder sb){
        boolean haveField = false;
        // 针对模糊搜索和大小判断
        if(value instanceof String){
            String valueStr = String.class.cast(value);
            if(StringUtils.isEmpty(valueStr)){
                return false;
            }
            // 设置模糊搜索
            if(valueStr.startsWith(LIKE_PRE)){
                valueList.add(getLikeValue(valueStr));
                sb.append(MessageFormat.format("{0} like '?' and ", field));
                return true;
            }

            // 大小比较设置，针对 ">", "<", ">=", "<=" 这么几个进行比较
            if (fieldIsNumber(field) && haveThanPre(valueStr)){
                valueList.add(getThanType(valueStr));
                valueList.add(NumberUtils.createNumber(getThanValue(valueStr)));
                sb.append(MessageFormat.format("{0} %s ? and ", field));
                return true;
            }
        } else if(value instanceof List){
            List values = List.class.cast(value);
            if (CollectionUtils.isEmpty(values)){
                return false;
            }
            // 判断当前类型是否属于时间类型
            if (fieldIsDate(field)){
                if(values.size() == 2){
                    valueList.add(String.valueOf(values.get(0)));
                    valueList.add(String.valueOf(values.get(1)));
                    sb.append(MessageFormat.format("({0} between ? and ?) and ", field));
                    return true;
                }
            }
        }
        valueList.add(value);
        sb.append(MessageFormat.format("{0}=? and ", field));
        return true;
    }

    private String getMaxDate(){
        Date date = new Date(Long.MAX_VALUE);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    private String getLikeValue(String likeValue){
        if(likeValue.startsWith(LIKE_PRE)){
            return likeValue.substring(likeValue.indexOf(LIKE_PRE) + LIKE_PRE.length()) + "%";
        }
        return likeValue;
    }

    private String getThanValue(String fieldValue){
        if(haveThanPre(fieldValue)){
            if(fieldValue.startsWith(">")){
                if(fieldValue.startsWith(">=")){
                    return fieldValue.substring(fieldValue.indexOf(">=") + ">=".length());
                }else{
                    return fieldValue.substring(fieldValue.indexOf(">") + ">".length());
                }
            }else if(fieldValue.startsWith("<")){
                if(fieldValue.startsWith("<=")){
                    return fieldValue.substring(fieldValue.indexOf("<=") + "<=".length());
                }else{
                    return fieldValue.substring(fieldValue.indexOf("<") + "<".length());
                }
            }
        }
        return fieldValue;
    }

    private String getThanType(String fieldValue){
        if(haveThanPre(fieldValue)){
            if(fieldValue.startsWith(">")){
                if(fieldValue.startsWith(">=")){
                    return ">=";
                }else{
                    return ">";
                }
            }else if(fieldValue.startsWith("<")){
                if(fieldValue.startsWith("<=")){
                    return "<=";
                }else{
                    return "<";
                }
            }
        }
        return fieldValue;
    }

    /**
    * 判断当前数性查询的属性是否为时间字段
    */
    private boolean fieldIsDate(String field){
        List<Column> columns = tableColumn.get(getTableName());
        if(!CollectionUtils.isEmpty(columns)){
            return columns.stream().filter(c->c.getName().equals(field)).anyMatch(column -> mysqlTimeType.contains(column.getType().toLowerCase()));
        }
        return false;
    }

    /**
    * 判断当前属性的字段是否为数字类型
    */
    private boolean fieldIsNumber(String field){
        List<Column> columns = tableColumn.get(getTableName());
        if(!CollectionUtils.isEmpty(columns)){
            return columns.stream().filter(c->c.getName().equals(field)).anyMatch(column ->
                  mysqlNumberType.contains(column.getType().toLowerCase()));
        }
        return false;
    }

    /**
    * 搜索的数据是否有比较类型的前缀
    */
    private boolean haveThanPre(String value){
        if (StringUtils.isEmpty(value)) {
            return false;
        }

        for(String pre :thanPre){
            if(value.startsWith(pre)){
                return true;
            }
        }
        return false;
    }

    /**
    * 根据map 分页的数据生成limit 对应的一些数据
    * @param record 待分页的参数数据
    * @return
    * 比如：limit 1,30
    */
    String getLimitStr(Record record){
        Record pager = Record.of((Map) record.get("pager"));
        Integer pageNo = pager.getInt("pageNo");
        Integer pageSize = pager.getInt("pageSize");
        Integer pageStart = (pageNo - 1) * pageSize;
        return String.format("limit %d, %d", pageStart, pageSize);
    }
}
