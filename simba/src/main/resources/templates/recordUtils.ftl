package ${appPath}.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import me.zzp.am.Record;
import org.springframework.util.CollectionUtils;

/**
 * @author robot
 */
@UtilityClass
public class RecordUtils {

    public boolean isEmpty(Record record){
        return (null == record) || record.isEmpty();
    }

    /**
     * 针对其中的一些类型进行转换，这了用于后端传递过去的Long类型的精度丢失，转换为String类型
     */
    public List<Record> typeChg(List<Record> dataList){
        if(CollectionUtils.isEmpty(dataList)){
            return new ArrayList<>();
        }
        return dataList.stream().filter(d->!d.isEmpty()).map(RecordUtils::longValueToString).collect(Collectors.toList());
    }

    /**
    * 将value为long的情况转换为String，前端接收的精度有问题
    */
    private Record longValueToString(Record record){
        Map<String, Object> data = record.entrySet().stream().filter(entry -> null != entry.getValue()).peek(entry->{
            Object value = entry.getValue();
            if(value instanceof Long){
                value = String.valueOf(value);
            }
            entry.setValue(value);
        }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        record.putAll(data);
        return record;
    }

    /**
    * 从userPo中找到相应属性，填充到recordList的每个对象里。
    * @param recordList 要填充的数据列表
    * @param recordUserIdName 填充根据用户ID在record中的key
    * @param transMap key=放到record中的key value = 用户record的中的属性
    */
    public void fillProperties(List<Record> recordList, Map<Long, Record> userMap, String recordUserIdName, Map<String,String> transMap) {
        if(recordList != null) {
            recordList.forEach(record -> {
                Record userRecord = userMap.get(record.getLong(recordUserIdName));
                if (!RecordUtils.isEmpty(userRecord)) {
                    for (Map.Entry<String, String> entry : transMap.entrySet()) {
                        record.put(entry.getKey(), userRecord.getStr(entry.getValue()));
                    }
                }
            });
        }
    }
}
