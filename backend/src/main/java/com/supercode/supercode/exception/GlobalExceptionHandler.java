package com.supercode.supercode.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.supercode.supercode.vo.MessageVO;
import com.supercode.supercode.vo.ResultVO;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = SupercodeException.class)
    public ResultVO<MessageVO> handleAllExternalException(SupercodeException e) {
        // e.printStackTrace();
        return ResultVO.buildFailure(e.getMessage());
    }
}
