package com.simon.simba.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/7 下午9:29
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class FieldInfo {
    /**
     * 属性的名字
     */
    @NonNull
    private String name;
    /**
     * 属性的描述
     */
    @NonNull
    private String desc;
    /**
     * 时间字段表示位：（0=不是时间，1=时间字段）
     */
    private Integer timeFlag = 0;
    /**
     * 时间字段表示位：（0=不是图片，1=是图片）
     */
    private Integer picFlag = 0;
    /**
     * 枚举字段表示位：（0=不是枚举，1=是枚举）
     */
    private Integer enumFlag = 0;

    public static FieldInfo of(String name){
        return new FieldInfo().setName(name);
    }
}
