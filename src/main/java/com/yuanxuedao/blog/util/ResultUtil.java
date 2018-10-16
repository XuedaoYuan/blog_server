package com.yuanxuedao.blog.util;

public class ResultUtil {
    //请求返回
    public static Msg success(Object object, String message, boolean success){
        Msg msg = new Msg();
        msg.setData(object);
        msg.setMessage(message);
        msg.setSuccess(success);
        return msg;
    }
    public static Msg success(Object object){
        Msg msg = new Msg();
        msg.setData(object);
        msg.setMessage("");
        msg.setSuccess(true);
        return msg;
    }
    public static Msg success(){
        Msg msg = new Msg();
        msg.setMessage("");
        msg.setSuccess(true);
        return msg;
    }
    public static Msg error(String message){
        Msg msg = new Msg();
        msg.setMessage(message);
        msg.setSuccess(false);
        return msg;
    }
}
