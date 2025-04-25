package org.example.healbackend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.healbackend.bean.User;

@Mapper
public interface UserMapper {


    // 只根据用户名查询用户（用于注册时检查是否存在）
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    // 根据用户名和密码查询用户（用于登录验证）
    @Select("SELECT * FROM users WHERE username = #{username} AND password = #{password}")
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    // 根据ID查询用户
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(@Param("id") int id);
    // 查询所有用户
    @Select("SELECT * FROM users")
    java.util.List<User> findAll();
    // 插入新的用户
    @Insert("INSERT INTO users (username, password, avatar_url, role, created_at) VALUES (#{username}, #{password}, #{email}, #{phoneNumber}, #{avatarUrl}, #{bio}, #{role}, #{createdAt}, #{userAvatar})")
    int insertUser(User user);
    // 更新用户信息
    @Update("UPDATE users SET username = #{username}, password = #{password}, email = #{email}, phone_number = #{phoneNumber}, avatar_url = #{avatarUrl}, bio = #{bio}, role = #{role}, user_avatar = #{userAvatar} WHERE id = #{id}")
    int updateUser(User user);
    // 根据ID删除用户
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteUser(@Param("id") int id);
    
}
