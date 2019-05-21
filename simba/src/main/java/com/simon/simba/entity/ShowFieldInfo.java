package com.simon.simba.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/2 下午10:59
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ShowFieldInfo {

    private FieldInfo fieldInfo;
    /**
     * 属性在界面的占比大小
     */
    private Integer rate = 20;

    public static ShowFieldInfo of(String name, String desc){
        return new ShowFieldInfo().setFieldInfo(FieldInfo.of(name, desc));
    }

    public static ShowFieldInfo of(String name){
        return new ShowFieldInfo().setFieldInfo(FieldInfo.of(name));
    }
}
