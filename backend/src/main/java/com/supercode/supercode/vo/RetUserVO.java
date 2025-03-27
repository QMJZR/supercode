package com.supercode.supercode.vo;

import io.micrometer.common.lang.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RetUserVO {
    private String username;
    private String name;
    @Nullable
    private String avatar;
    private String role;
    @Nullable
    private String telephone;
    @Nullable
    private String email;
    @Nullable
    private String location;
}
