package com.supercode.supercode.exception;

public class SupercodeException extends RuntimeException {
    public SupercodeException(String message) {
        super(message);
    }

    public static SupercodeException createFail() {
        throw new SupercodeException("创建用户失败");
    }

    public static SupercodeException loginRequired() {
        throw new SupercodeException("请登录");
    }

    public static SupercodeException userExisted() {
        throw new SupercodeException("用户名已存在");
    }

    public static SupercodeException loginFailure() {
        throw new SupercodeException("用户不存在/用户密码错误");
    }

    public static SupercodeException updateFailed() {
        throw new SupercodeException("更新用户信息失败");
    }

    public static Exception userNotExisted() {
        throw new SupercodeException("用户不存在");
    }
}
