package org.example.healbackend.service;

import org.example.healbackend.mapper.UserMapper;
import org.example.healbackend.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    // 注册用户
    public boolean register(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return false;
        }
        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            return false;
        }
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return userMapper.insertUser(user) > 0;
    }

    // 登录
    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        return userMapper.findByUsernameAndPassword(username, password);
    }

    // 根据用户名查找
    public User getUserByName(String username) {
        return userMapper.findByUsername(username);
    }

    // 根据ID查找
    public User findById(int userId) {
        return userMapper.findById(userId);
    }

    // 查询所有用户
    public List<User> findAll() {
        return userMapper.findAll();
    }

    // 更新用户信息
    public boolean updateUser(User user) {
        return userMapper.updateUser(user) > 0;
    }

    // 删除用户
    public boolean deleteUser(int userId) {
        return userMapper.deleteUser(userId) > 0;
    }
    public User getUserById(int id) {
        return userMapper.findById(id);
    }

}