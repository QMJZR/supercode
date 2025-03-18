package com.supercode.supercode.exception;

public class SupercodeException extends RuntimeException {
    public SupercodeException(String message) {
        super(message);
    }

    public static SupercodeException userExisted() {
        throw new SupercodeException("用户名已存在");
    }

    public static SupercodeException loginFailure() {
        throw new SupercodeException("用户不存在/用户密码错误");
    }

    public static SupercodeException userNotExisted() {
        throw new SupercodeException("用户不存在");
    }
}
