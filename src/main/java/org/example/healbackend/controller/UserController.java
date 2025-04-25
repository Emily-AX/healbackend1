package org.example.healbackend.controller;

import org.example.healbackend.Utils.TimeUtil;
import org.example.healbackend.bean.User;
import org.example.healbackend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    // 处理 用户注册 POST 请求
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Map<String, Object> register(@RequestBody(required = false) User user) {
        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("result", false);
            response.put("message", "请求体缺失");
            return response;
        }

        if (user.getUsername() == null || user.getPassword() == null) {
            response.put("result", false);
            response.put("message", "请输入用户名和密码!");
            return response;
        }

        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            response.put("result", false);
            response.put("message", "用户名已存在!");
        } else {
            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            userMapper.insertUser(new User(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), createdAt,user.getIs_online()));
            response.put("result", true);
            response.put("message", "注册成功!");
        }
        return response;
    }
    // 用户 登录 模块
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String, Object> login(@RequestBody(required = false) User user) {
        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("result", false);
            response.put("message", "请求体缺失");
            return response;
        }

        if (user.getUsername() == null || user.getPassword() == null) {
            response.put("result", false);
            response.put("message", "请输入用户名和密码!");
            return response;
        }

        User existingUser = userMapper.findByUsernameAndPassword(user.getUsername(),user.getPassword());
        if (existingUser == null) {
            response.put("result", false);
            response.put("message", "用户不存在!");
        } else if (!existingUser.getPassword().equals(user.getPassword())) {
            response.put("result", false);
            response.put("message", "密码错误!");
        } else {
            response.put("result", true);
            response.put("message", "登录成功!");
            response.put("username", existingUser.getUsername());
            response.put("role", existingUser.getRole());
            response.put("createdAt", existingUser.getCreatedAt());
            response.put("userId", existingUser.getId());
            System.out.println("role: " + existingUser.getRole());
            System.out.println("createdAt: " + existingUser.getCreatedAt());
            System.out.println("userId: " + existingUser.getId());
            handleSuccessLogin(user.getUsername());
        }
        return response;
    }

    private void handleSuccessLogin(String username) {
        // 获取当前时间
        String loginTime = TimeUtil.getCurrentTimeString();

        // 记录登录日志
        System.out.println("用户 " + username + " 登录成功，时间：" + loginTime);
    }
}
