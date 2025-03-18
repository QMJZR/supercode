package com.supercode.supercode.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.supercode.supercode.exception.SupercodeException;
import com.supercode.supercode.po.User;
import com.supercode.supercode.repository.UserRepository;
import com.supercode.supercode.service.UserService;
import com.supercode.supercode.util.TokenUtil;
import com.supercode.supercode.vo.LoginResultVO;
import com.supercode.supercode.vo.MessageVO;
import com.supercode.supercode.vo.UserVO;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenUtil tokenUtil;

    @Override
    public UserVO getUserDetail(String username) {
        try {
            return userRepository.findByUsername(username).toVO();
        } catch (Exception e) {
            throw SupercodeException.userNotExisted();
        }
    }

    @Override
    public MessageVO createUser(UserVO user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw SupercodeException.userExisted();
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user.toPO());
        return new MessageVO("创建用户成功");
    }

    @Override
    public LoginResultVO login(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password);
        if (user != null) {
            return new LoginResultVO("登录成功", tokenUtil.getToken(user));
        }
        throw SupercodeException.loginFailure();
    }

    @Override
    public MessageVO update(UserVO userVO) {
        try {
            userRepository.delete(userRepository.findByUsername(userVO.getUsername()));
            userRepository.save(userVO.toPO());
            return new MessageVO("用户信息更新成功");
        } catch (Exception e) {
            throw SupercodeException.userNotExisted();
        }
    }

}
