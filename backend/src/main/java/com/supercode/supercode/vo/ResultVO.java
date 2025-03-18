package com.supercode.supercode.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResultVO<T> implements Serializable {
    private String code;
    private T data;

    public static <T> ResultVO<T> buildSuccess(T data) {
        return new ResultVO<T>("000", data);
    }

    public static <T> ResultVO<MessageVO> buildFailure(String message) {
        return new ResultVO<MessageVO>("400", new MessageVO(message));
    }
}