package com.simon.simba.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/9 下午3:29
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class EnumMeta {
    @NonNull
    private String name;
    @NonNull
    private String desc;
}
