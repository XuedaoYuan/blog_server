package com.yuanxuedao.blog.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuanxuedao.blog.dao.BlogsDao;
import com.yuanxuedao.blog.dao.LoginTicketDao;
import com.yuanxuedao.blog.dao.UserDao;
import com.yuanxuedao.blog.pojo.Blogs;
import com.yuanxuedao.blog.pojo.LoginTicket;
import com.yuanxuedao.blog.pojo.User;
import com.yuanxuedao.blog.util.CookieUtil;
import com.yuanxuedao.blog.util.Log;
import com.yuanxuedao.blog.util.Msg;
import com.yuanxuedao.blog.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
public class BlogsController {
    @Autowired
    BlogsDao blogsDao;

    @Autowired
    UserDao userDao;

    @Autowired
    LoginTicketDao loginTicketDao;

    private static String STICK_COOKIE_NAME = "ticket";
    private static String ADMIN_ROLE = "admin";

    public String getRoleByStick(HttpServletRequest request){
        Cookie cookie = CookieUtil.getCookieByName(request, STICK_COOKIE_NAME);
        if(null == cookie){
            return null;
        }
        String stick = cookie.getValue();
        Log.logger.info("getRoleByStick:" + stick);
        LoginTicket loginTicket = loginTicketDao.seletByTicket(stick);
        int userId = loginTicket.getUserId();
        User user = userDao.seletById(userId);
        return user.getRole();
    }


    /*
    * 分页查询博客
    * */
    @GetMapping("/blogs")
    public Msg listBlogs(
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "limit", defaultValue = "5") int limit
    ){
        PageHelper.startPage(start, limit, "id desc");
        List<Blogs> bs = blogsDao.selectAll();
        PageInfo<Blogs> page = new PageInfo<>(bs);
        return ResultUtil.success(page);
    }

    /*
    * 分页查询博客标题
    * */
    @GetMapping("/blogs/title")
    public Msg listBlogTitles(
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "limit", defaultValue = "5") int limit
    ){
        PageHelper.startPage(start, limit, "id desc");
        List<Blogs> bs = blogsDao.selectAllTitles();
        PageInfo<Blogs> page = new PageInfo<>(bs);
        return ResultUtil.success(page);
    }

    /*
    * 根据ID查询
    * */
    @GetMapping("/blogs/id")
    public Msg findById(@RequestParam int id){

        Blogs blogs = blogsDao.selectById(id);

        return ResultUtil.success(blogs);
    }


    /*
    * 发布新增博客
    *
    * */
    @PostMapping("/blogs")
    public Msg addBlog(HttpServletRequest request, @RequestBody Blogs blogs){

        //管理员才可以发布博客， 需要role="admin"
        String role = getRoleByStick(request);
        if(!role.equals(ADMIN_ROLE)){
            return ResultUtil.error("管理员才可以发布");
        }

        //设置创建日期
        Date date = new Date();
        blogs.setCreatetime(date);
        blogsDao.insertBlogs(blogs);

        return ResultUtil.success("发布成功");
    }

    /*
    * 修改博客
    *
    * */
    @PostMapping("/blogs/update")
    public Msg updateBlog(@RequestBody Blogs blogs){
        Date date = new Date();
        blogs.setCreatetime(date);
        blogsDao.updateById(blogs);
        return ResultUtil.success();
    }

    /*
    * 根据ID删除博客
    * */
    @DeleteMapping("/blogs")
    public Msg deleteBlog(@RequestParam int id){
        blogsDao.deleteById(id);
        return ResultUtil.success();
    }
}
