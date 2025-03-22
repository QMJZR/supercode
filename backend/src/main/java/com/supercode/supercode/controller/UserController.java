package com.supercode.supercode.controller;

import com.supercode.supercode.po.LoginResult;
import com.supercode.supercode.vo.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.supercode.supercode.service.UserService;

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
    public ResultVO<RetUserVO> getUserDetail(@PathVariable String username) throws Exception {
        return ResultVO.buildSuccess(userService.getUserDetail(username));
    }

    @PostMapping("")
    public ResultVO<MessageVO> createUser(@RequestBody UserVO userVO) {
        return ResultVO.buildSuccess(userService.createUser(userVO));
    }

    @PostMapping("/login")
    public ResultVO<LoginResultVO> login(HttpServletResponse request,@RequestBody LoginVO loginVO) {
        LoginResult result=userService.login(loginVO.getUsername(), loginVO.getPassword());
        LoginResultVO resultVO=result.toVO();
        Cookie cookie=new Cookie("token",result.getToken());
        cookie.setPath("/");
        cookie.setMaxAge(24*60*60);
        request.addCookie(cookie);
        return ResultVO.buildSuccess(resultVO);
    }

    @PutMapping("")
    public ResultVO<MessageVO> putMethodName(@RequestBody UserVO userVO) {
        return ResultVO.buildSuccess(userService.update(userVO));
    }
}
