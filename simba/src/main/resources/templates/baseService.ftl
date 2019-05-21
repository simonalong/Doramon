package ${appPath}.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ${appPath}.dao.BaseDao;
import ${appPath}.util.RecordUtils;
import ${idGenPath};
import me.zzp.am.Record;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.TimeZone;
import java.util.stream.Stream;

/**
 * @author robot
 */
public abstract class BaseService {

    protected abstract BaseDao getDao();

    @Autowired
    private IdGeneratorServer idGeneratorServer;

    public Integer insert(Record record) throws Exception {
        if (!record.containsKey("id")) {
            record.set("id", idGeneratorServer.genLid());
        }
        return getDao().insert(record);
    }

    /**
     * 针对有时间类型的字符的转换
     *
     * @param record 数据记录
     * @param timeKeys 时间字段的key
     */
    public Integer insert(Record record, String... timeKeys) throws Exception {
        if (!record.containsKey("id")) {
            record.set("id", idGeneratorServer.genLid());
        }

        timeStrTypeChg(record, timeKeys);
        return getDao().insert(record);
    }

    public Integer delete(Long id) throws Exception {
        return getDao().delete(id);
    }

    public Integer update(Record record) throws Exception {
        return getDao().update(record);
    }

    public Integer update(Record record, String... timeKeys) throws Exception {
        timeStrTypeChg(record, timeKeys);
        return getDao().update(record);
    }

    public Record one(Long id){
        return getDao().one(id);
    }

    /**
     * 针对后端向前端传递Long类型的精度丢失问题，这里将所有的Long类型全部通过String类型进行转换
     */
    public List<Record> getPage(Record record) {
        return getDao().getPage(record);
    }

    public Integer count(Record record) {
        return getDao().count(record);
    }

    /**
    * 从记录的表中，获取外表的主键，然后将主键数据扩展并重新加入到集合中
    *
    * 注意：这里所有的表的主键key默认都为id
    *
    * @param recordList 要填充的数据列表
    * @param recordUserIdName 填充根据用户ID在record中的key
    * @param transMap key=放到record中的key value = 查询record的中的属性
    */
    public void fillCommonProperties(List<Record> recordList, String recordUserIdName, Map<String,String> transMap) {
        Map<Long, Record> userMap = getDao().getListByIds(recordList.stream().map(record1 -> record1.getLong(recordUserIdName)).collect(Collectors.toList()));
        RecordUtils.fillProperties(recordList, userMap, recordUserIdName, transMap);
    }

    /**
    * 时间类型值的转换
    *
    * @param record 记录
    * @param timeKeys 事件类型的列
    */
    private void timeStrTypeChg(Record record, String... timeKeys){
        if (null != timeKeys) {
            SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            //设置时区UTC
            parse.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Z")));
            Stream.of(timeKeys).forEach(key -> {
                if (record.containsKey(key)) {
                try {
                        record.put(key, format.format(parse.parse(String.valueOf(record.get(key)))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
