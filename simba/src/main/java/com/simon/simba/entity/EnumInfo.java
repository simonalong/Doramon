package com.simon.simba.entity;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/9 下午2:26
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class EnumInfo {
    @NonNull
    private String fieldName;
    @NonNull
    private List<EnumMeta> metaList;
}
