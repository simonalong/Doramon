package com.simon.simba.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/3 下午8:38
 */
@Data
@Accessors(chain = true)
public class UpdateAddFieldInfo {

    private FieldInfo fieldInfo;
    /**
     * 属性是否可以更新编辑(0=不能编辑， 1=可以编辑)
     */
    private Integer canEdit = 1;
    /**
     * 属性更新是否是必需(0=不需要，1=需要)
     */
    private Integer require = 0;

    public static UpdateAddFieldInfo of(String name, String desc){
        return new UpdateAddFieldInfo().setFieldInfo(FieldInfo.of(name, desc));
    }
}
