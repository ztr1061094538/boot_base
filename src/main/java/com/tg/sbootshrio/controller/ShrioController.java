package com.tg.sbootshrio.controller;

import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.tg.sbootshrio.annotation.MyLog;
import com.tg.sbootshrio.mapper.UserMapper;
import com.tg.sbootshrio.pojo.User;
import com.tg.sbootshrio.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


@Controller
public class ShrioController {
    @Autowired(required = true)
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/toindex")
    @MyLog(title = "登陆模块", action = "登陆操作")
    public String toIndex() {
        return "index";
    }


    @PostMapping("/testRedis")
    @ResponseBody
    public List<User> testRedis() {
        List<User> users = userService.getUser();
        redisTemplate.opsForValue().set("users", users);
        List<User> users2 = (List<User>) redisTemplate.opsForValue().get("users");
        System.out.println("users2 = " + users2);
        return users;
    }


    @PostMapping("/testPost")
    @ResponseBody
    public User testPost(@RequestBody User user) {
        System.out.println("user = " + user);

        List<User> users = userService.getUser();
        return users.get(0);
    }


    @GetMapping("/testJson")
    @ResponseBody
    public String testJson() {
        System.out.println(" jintu     testJson...... ");

        List<User> users = userService.getUser();
        String jsonString = JSON.toJSONString(users);
        System.out.println("jsonString = " + jsonString);
        JSONArray jsonArray = JSONArray.parseArray(jsonString);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String userName = jsonObject.getString("userName");
            System.out.println("userName = " + userName);
            String password = jsonObject.getString("password");
            System.out.println("password = " + password);
        }

        return null;
    }


    @GetMapping("/templateusers")
    @ResponseBody
    public String templateusers(String name) {


        // User user = JSON.parseObject(name, User.class);
        System.out.println("name = " + name);
        //String jsonString = JSON.toJSONString(name);
        User user = JSON.parseObject(name, User.class);

        System.out.println("user = " + user);
        //    JSONObject jsonObject = (JSONObject)JSONObject.parse(jsonString);
//        String password =(String) jsonObject.get("password");
        //  System.out.println("password = " + password);
//        String o = (String)jsonObject.get("u");
//
//        User parse = (User)JSON.parse(o);
//
//        System.out.println("parse = " + parse);
        List<User> users = userService.getUser();
        String jsonString1 = JSON.toJSONString(users);
        return jsonString1;
    }


    @GetMapping("/users")
    @ResponseBody
    public String users(Model model, HttpServletRequest request) {


        List<String> jsons = new ArrayList<>();
        jsons.add("aaa");
        jsons.add("bbb");
        String jsonString = JSON.toJSONString(jsons);
        System.out.println("jsonString = " + jsonString);


        List<User> users = userService.getUser();
        String jsonString1 = JSONArray.toJSONString(users);
        //String jsonString1 = JSON.toJSONString(users);都可以
        //Staff staff = JSON.parseObject(jsonString, Staff.class);
        System.out.println("jsonString1 = " + jsonString1);

        JSONObject json = new JSONObject();
        json.put("users", jsonString1);
        json.put("a", "b");

        String userString = (String) json.get("users");


        List<User> users1 = JSON.parseArray(userString, User.class);

        System.out.println("users1:" + users1);

        return jsonString1;
    }


    @RequestMapping("/add")
    public String add(HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("id");
        System.out.println("add    username:" + username);
        return "user/add";
    }

    @RequestMapping("/update")
    public String update(Model model) {
        return "user/update";
    }


    @RequestMapping("/tologin")
    public String tologin() {
        return "login";
    }

    /**
     *
     */
    @PostMapping("/login")
    public String login(String username, String password, String remmeber, HttpServletRequest request
            , HttpServletResponse response) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            subject.login(token);
            User user = new User();
            user.setUserName(username);
            List<User> select = userMapper.select(user);
            Long userId = select.get(0).getId();
            HttpSession session =
                    request.getSession();
            session.setAttribute("userId", userId+"");
            session.setMaxInactiveInterval(60*60);//應該是1小时
            if (remmeber != null && remmeber.equals("y")) {//勾选了记住密码
                Cookie nameAndPasscookie = new Cookie(username, password);
                nameAndPasscookie.setMaxAge(7 * 24 * 60 * 60);   //存活期为7天
                nameAndPasscookie.setPath("/");
                response.addCookie(nameAndPasscookie);
                /**
                 * cookies 的格式是：
                 *   zhan=123123;weixiaoyan=123123
                 * 所以前台  首先要根据 ‘;’ 拆，然后根据“=”拆，然后遍历对比 取值
                 */
            } else {
                Cookie[] cookies = request.getCookies();
                if (cookies != null && cookies.length > 0) {
                    for (Cookie cookie : cookies) {
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
            }
            return "index";
        } catch (UnknownAccountException e) {
            //用户名不存在
            return "login";
        } catch (IncorrectCredentialsException e) {
            //密码错误
            return "login";
        }

    }
}
