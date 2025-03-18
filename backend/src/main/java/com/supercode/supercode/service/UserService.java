package com.supercode.supercode.service;

import org.springframework.stereotype.Service;

import com.supercode.supercode.vo.LoginResultVO;
import com.supercode.supercode.vo.MessageVO;
import com.supercode.supercode.vo.UserVO;

@Service
public interface UserService {

    UserVO getUserDetail(String username);

    MessageVO createUser(UserVO user);

    LoginResultVO login(String username, String password);

    MessageVO update(UserVO user);
}
