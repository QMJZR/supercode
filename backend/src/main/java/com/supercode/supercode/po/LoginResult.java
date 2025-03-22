package com.supercode.supercode.po;

import com.supercode.supercode.vo.LoginResultVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResult {
    private String msg;
    private String token;
    public LoginResultVO toVO(){
        LoginResultVO loginResultVO=new LoginResultVO();
        loginResultVO.setMsg(msg);
        return loginResultVO;
    }
}
