package com.supercode.supercode.serviceImpl;

import cn.hutool.core.lang.Validator;
import com.supercode.supercode.po.LoginResult;
import com.supercode.supercode.vo.RetUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.supercode.supercode.exception.SupercodeException;
import com.supercode.supercode.po.User;
import com.supercode.supercode.repository.UserRepository;
import com.supercode.supercode.service.UserService;
import com.supercode.supercode.util.TokenUtil;
import com.supercode.supercode.vo.MessageVO;
import com.supercode.supercode.vo.UserVO;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenUtil tokenUtil;

    @Override
    public RetUserVO getUserDetail(String username) throws Exception {
        try {
            return userRepository.findByUsername(username).toRetVO();
        } catch (Exception e) {
            throw SupercodeException.userNotExisted();
        }
    }

    @Override
    public MessageVO createUser(UserVO user) {
        if(user.getName()==null)
            throw SupercodeException.createFail();
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw SupercodeException.userExisted();
        }
        if(user.getTelephone() != null && (user.getTelephone().charAt(0) != '1' || user.getTelephone().length() != 11))
            throw SupercodeException.createFail();
        if(user.getEmail()!=null&&!Validator.isEmail(user.getEmail()))
            throw SupercodeException.createFail();
        userRepository.save(user.toPO());
        return new MessageVO("创建用户成功");
    }

    @Override
    public LoginResult login(String username, String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User user = userRepository.findByUsername(username);
        if (user != null&&bCryptPasswordEncoder.matches(password,user.getPassword())) {
            return new LoginResult("登录成功", tokenUtil.getToken(user));
        }
        throw SupercodeException.loginFailure();
    }

    @Override
    public MessageVO update(UserVO userVO) {
        try {
            User user=userRepository.findByUsername(userVO.getUsername());
            if(userVO.getName()!=null)
                user.setName(userVO.getName());
            user.setAvatar(userVO.getAvatar());
            if(userVO.getTelephone() != null && (userVO.getTelephone().charAt(0) != '1' || userVO.getTelephone().length() != 11))
                throw SupercodeException.updateFailed();
            user.setTelephone(userVO.getTelephone());
            if(userVO.getEmail()!=null&&!Validator.isEmail(userVO.getEmail()))
                throw SupercodeException.updateFailed();
            user.setEmail(userVO.getEmail());
            user.setLocation(userVO.getLocation());
            userRepository.save(user);
            return new MessageVO("用户信息更新成功");
        } catch (Exception e) {
            throw SupercodeException.updateFailed();
        }
    }

}
