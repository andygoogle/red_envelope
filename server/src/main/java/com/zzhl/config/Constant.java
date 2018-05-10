package com.zzhl.config;

/**
 * 常量定义类
 * <p>Created: 2017-08-25</p>
 *
 * @author andy
 **/
public class Constant {
    public static String WX_ACCESS_TOKEN = "";      // 微信小程序接口请求access_token
    public static long WX_ACCESS_TOKEN_EXPIRES = 0;  // 微信小程序接口请求access_token 到期时间

    /**
     * 用户类型， 0-微信，1-用户增加(僵尸用户)
     */
    public static final byte USER_TYPE_WECHAT = 0;
    public static final byte USER_TYPE_ZOMBIE = 1;

    public static final String USER_ID = "USER_ID";
    public static final String SESSION = "SESSION";
}
