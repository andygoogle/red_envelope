package com.zzhl.exception;

import com.zzhl.exception.ErrorCode;

/**
 * 消息处理
 * <p>Created: 2017-08-25</p>
 *
 * @author andy
 **/
public class CodeException extends RuntimeException {

    protected int code;

    protected String msg;

    public CodeException(ErrorCode errorCode) {
        this.code = errorCode.getState();
        this.msg = errorCode.getMessage();
    }

    public CodeException(int code, String msgs) {
        this.code = code;
        this.msg = msgs;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String getMessage() {
        return getMsg();
    }

}
