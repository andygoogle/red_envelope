package com.zzhl.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zzhl.exception.ErrorCode;
import com.zzhl.util.JSONUtil;

import java.io.IOException;

/**
 * 返回结果
 * <p>Created: 2017-02-20</p>
 *
 * @author andy
 **/
public class BaseResult<T> {

    private int resultCode = ErrorCode.SUCCESS.getState();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resultMessage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object resultData;

    public BaseResult(){
    }

    public BaseResult(Object resultData) {
        this.resultData = resultData;
    }

    public BaseResult(int resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    public BaseResult(int resultCode, String resultMessage, Object resultData) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.resultData = resultData;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Object getResultData() {
        return resultData;
    }

    public void setResultData(Object resultData) {
        this.resultData = resultData;
    }

    @Override
    public String toString() {
        try {
            return JSONUtil.bean2Json(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
