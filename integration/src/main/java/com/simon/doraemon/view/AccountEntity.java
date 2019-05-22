package com.simon.doraemon.view;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author robot
 */
@Data
@RequiredArgsConstructor(staticName = "of")
public class AccountEntity {

    @NonNull
    private String name;
    @NonNull
    private String password;
    @NonNull
    private String authority;
    private String type;
}
