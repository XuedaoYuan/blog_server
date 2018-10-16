package com.yuanxuedao.blog.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
* Cookie简介
*  1、name/value
*   cookie的键值对， 存储名称和值
*  2、Expires 设置一个Cookie失效的绝对时间， 早于当前时间就会删除， 兼容性好
*  3、Max-age 设置秒数， Cookie会在多少秒后过期， IE不支持。 负数（-1）会在浏览器关闭后失效， 0 马上清除Cookie， 正数就是设置过期时间
*  4、path cookie的有效路径， '/'表示工程下都可以访问， 否则只能是当前路径和子路径允许访问
*  5、Domain 设置哪些域下的Web服务器可以读取cookie，
*  6、Secure 设置为true表示只有https才可以提交cookie
* */


public class CookieUtil {
    /*
    * 获取Cookies集合
    * */
    public static Cookie[] getCookies(HttpServletRequest request){
        return request.getCookies();
    }

    /*
    * 根据指定名称获取Cookie
    * */
    public static Cookie getCookieByName(HttpServletRequest request, String name){
        if(StringUtils.isEmpty(name)){
            return null;
        }
        Cookie[] cookies = getCookies(request);
        if(null != cookies){
            for(Cookie cookie : cookies){
                if(name.equals(cookie.getName())){
                    return cookie;
                }
            }
        }

        return null;

    }

    /*
    * 添加cookie
    * */
    public static boolean addCookie(HttpServletResponse response, String name, String value, int maxAge){
        if(StringUtils.isEmpty(name) || StringUtils.isEmpty(value)){
            return false;
        }
        Cookie cookie = new Cookie(name.trim(), value.trim());
        if(maxAge <= 0){
            maxAge = Integer.MAX_VALUE;
        }
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        response.addCookie(cookie);
        return true;
    }

    /*
    * 删除cookie
    * */
    public static boolean removeCookie(HttpServletRequest request, HttpServletResponse response, String name){
        if(StringUtils.isEmpty(name)){
            return false;
        }
        Cookie[] cookies = getCookies(request);
        if(null != cookies){
            for(Cookie cookie : cookies){
                if(name.equals(cookie.getName())){
                    cookie.setValue(null);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    return true;
                }
            }
        }
        return false;

    }
}
