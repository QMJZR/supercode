package com.supercode.supercode.vo;

import com.supercode.supercode.po.User;

import io.micrometer.common.lang.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserVO {
    @Nullable
    private String uuid;
    private String username;
    private String password;
    private String name;
    @Nullable
    private String avator;
    @Nullable
    private String telephone;
    @Nullable
    private String email;
    @Nullable
    private String location;

    public User toPO() {
        User user = new User();
        if (uuid != null) {
            user.setUuid(uuid);
        }
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        if (avator != null) {
            user.setAvatar(avator);
        }
        if (telephone != null) {
            user.setTelephone(telephone);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (location != null) {
            user.setLocation(location);
        }
        return user;
    }
}
