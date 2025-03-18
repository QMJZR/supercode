package com.supercode.supercode.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.supercode.supercode.service.UserService;
import com.supercode.supercode.vo.LoginResultVO;
import com.supercode.supercode.vo.LoginVO;
import com.supercode.supercode.vo.MessageVO;
import com.supercode.supercode.vo.ResultVO;
import com.supercode.supercode.vo.UserVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/accounts")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{username}")
    public ResultVO<UserVO> getUserDetail(@PathVariable String username) {
        return ResultVO.buildSuccess(userService.getUserDetail(username));
    }

    @PostMapping("/")
    public ResultVO<MessageVO> createUser(@RequestBody UserVO userVO) {
        return ResultVO.buildSuccess(userService.createUser(userVO));
    }

    @PostMapping("/login")
    public ResultVO<LoginResultVO> login(@RequestBody LoginVO loginVO) {
        return ResultVO.buildSuccess(userService.login(loginVO.getUsername(), loginVO.getPassword()));
    }

    @PutMapping("/")
    public ResultVO<MessageVO> putMethodName(@RequestBody UserVO userVO) {
        return ResultVO.buildSuccess(userService.update(userVO));
    }
}
