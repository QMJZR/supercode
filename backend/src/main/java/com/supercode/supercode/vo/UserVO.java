package com.supercode.supercode.vo;

import cn.hutool.crypto.digest.DigestUtil;
import com.supercode.supercode.po.User;

import io.micrometer.common.lang.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.DigestUtils;

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
    private String avatar;
    private String role;
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
        String tmp=password+"-=[]"+username;
        user.setPassword(DigestUtil.sha512Hex(tmp));
        user.setName(name);
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        user.setRole(role);
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
