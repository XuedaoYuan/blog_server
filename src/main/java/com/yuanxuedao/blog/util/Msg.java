package com.yuanxuedao.blog.util;

public class Msg<T> {
    //提示信息
    private String message;

    //是否成功
    private boolean success;

    //具体内容
    private T data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
