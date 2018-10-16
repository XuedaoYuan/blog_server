package com.yuanxuedao.blog.controller;

import com.yuanxuedao.blog.dao.LoginTicketDao;
import com.yuanxuedao.blog.dao.UserDao;
import com.yuanxuedao.blog.pojo.LoginTicket;
import com.yuanxuedao.blog.pojo.User;
import com.yuanxuedao.blog.util.Log;
import com.yuanxuedao.blog.util.Msg;
import com.yuanxuedao.blog.util.ResultUtil;
import com.yuanxuedao.blog.util.XdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
public class UserController {

    @Autowired
    UserDao userDao;

    @Autowired
    LoginTicketDao loginTicketDao;
    /*
    * 注册
    * */
    public Map<String, String> register(String username, String password) {
        Map<String, String> map = new HashMap<>();

        if (StringUtils.isEmpty(username)) {
            map.put("msg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isEmpty(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }

        User u = userDao.seletByName(username);
        if(null != u){
            map.put("msg", "用户名已经被占用");
            return map;
        }

        User user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setHeadurl("暂时不用");
        user.setPassword(XdUtil.MD5(password + user.getSalt()));
        user.setRole("user");
        userDao.insertUser(user);

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    /*
    * 登录
    *
    * */

    public Map<String, String> login(String username, String password){


        Map<String, String> map = new HashMap<>();
        Random random = new Random();
        if (StringUtils.isEmpty(username)){
            map.put("msg","用户名不能为空");
            return map;
        }

        if (StringUtils.isEmpty(password)){
            map.put("msg","密码不能为空");
            return map;
        }

        User u = userDao.seletByName(username);
        /*验证用户是否存在*/
        if(null == u){
            map.put("msg", "用户名不存在");
            return map;
        }
        /*验证秘密是否正确*/
        if(!XdUtil.MD5(password + u.getSalt()).equals(u.getPassword())){
            map.put("msg", "密码错误");
            return map;
        }
        /*多个地方登陆需要删除上一次登录的凭证*/

        String ticket = addLoginTicket(u.getId());
        map.put("ticket", ticket);
        return map;

    }

    /*
    * 免密登录
    * */
    public String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 3600 * 30);
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);
        //生成凭证
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDao.insertLoginTicket(loginTicket);
        return loginTicket.getTicket();
    }


    /*
    * 登录接口
    * */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Msg loginApi(HttpServletResponse httpResponse, @RequestBody User users){
        Map<String, String> map = this.login(users.getName(), users.getPassword());
        //如果包含凭证， 就存入cookie
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket"));
            cookie.setPath("/");
            httpResponse.addCookie(cookie);
            return ResultUtil.success();
        }else {

            return ResultUtil.error(map.get("msg"));
        }
    }

    /*
    * 注册接口
    * */
    @PostMapping("/register")
    public Msg registerApi(HttpServletResponse httpServletResponse, @RequestBody User users){

        System.out.println(users.getName());

        Map<String, String> map = this.register(users.getName(), users.getPassword());

        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket"));
            cookie.setPath("/");
            httpServletResponse.addCookie(cookie);
            return ResultUtil.success(map.get("ticket"));

        }else {

            return ResultUtil.error(map.get("msg"));
        }

    }

    /*
    * 检查用户名是否已经被占用
    * */
    @GetMapping("/check/name")
    public Msg checkUsername(@RequestParam String name){
        User u = userDao.seletByName(name);
        if(null != u){
            return ResultUtil.error("用户名已被占用");
        }
        return ResultUtil.success();
    }

    /*
    * 返回用户信息
    * 根据ticket凭证（来自cookie）判断是否可以自动登录
    * */
    @GetMapping("/whoami")
    public Msg whoami(HttpServletRequest request) throws Exception{
        Cookie[] cookies = request.getCookies();
        if(null != cookies){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("ticket")){
                    String ticket = cookie.getValue();
                    if(StringUtils.isEmpty(ticket)){
                        return ResultUtil.error("需要重新登录");
                    }
                    LoginTicket lt = loginTicketDao.seletByTicket(ticket);
                    if(null == lt){
                        return ResultUtil.error("需要重新登录");
                    }
                    Date expired = lt.getExpired();
                    Date now = new Date();
                    //如果还没到 到期时间 说明可以自动登录 直接返回用户信息
                    if(expired.after(now)){
                        int userId = lt.getUserId();
                        Log.logger.info("获取到登录信息: "+ userId);
                        System.out.println("userID:" + userId);
                        //获取用户信息
                        User u = userDao.selectUserMsgById(userId);
                        return ResultUtil.success(u);

                    }
                }
            }
        }
        Log.logger.info("需要重新登录");
        return ResultUtil.error("登录失败，请重新登录");
    }

    /*
    * 退出登录
    * 重置cookie 删除ticket
    * */
    @GetMapping("/logout")
    public Msg logout(HttpServletRequest request, HttpServletResponse response){

        Cookie[] cookies = request.getCookies();
        if(null != cookies){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("ticket")){
                    String ticket = cookie.getValue();
                    LoginTicket lt = loginTicketDao.seletByTicket(ticket);
                    Log.logger.info("退出登录：" + ticket);
                    int ltId = lt.getId();
                    loginTicketDao.deleteById(ltId);
                    cookie.setValue(null);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }

        return ResultUtil.success();

    }
}
