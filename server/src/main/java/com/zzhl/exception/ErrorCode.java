package com.zzhl.exception;

/**
 * 返回结果错误代码
 * <p>Created: 2017-02-20</p>
 *
 * @author andy
 **/
public enum ErrorCode {
    SUCCESS(1000, ""),     // 操作成功
    ERROR(1, "操作失败"),
    SYSTEM_ERROR(2, "系统错误"),
    THIRD_RETURN_ERROR(3, "第三方调用失败"),

    /**
     * 请求参数校验
     */
    PARAMS_ERROR(2000, "参数错误"),
    PARAMS_TYPE_ERROR(2001, "参数类型错误"),

    DB_ERROR(3000, "数据库错误"),

    /**
     * 业务错误代码,从10000 开始定义
     */
    GAME_ERROR(10000, ""),
    GAME_NO_FOUND(10001, ""),
    JOINED_FAIL(10002, "加入牌局失败"),
    
    /** 音频检测 **/
    Audio_Check_Error(20001,"语音检测未通过");


    private final int state;
    private final String message;

    ErrorCode(int state, String message) {
        this.state = state;
        this.message = message;
    }

    public int getState() {
        return this.state;
    }

    public String getMessage() {
        return this.message;
    }

    public static ErrorCode get(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.state == code) {
                return errorCode;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + code + "]");
    }

    @Override
    public String toString() {
        return Integer.toString(this.state);
    }

}
