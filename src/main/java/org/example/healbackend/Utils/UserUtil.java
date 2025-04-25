package org.example.healbackend.Utils;

import org.example.healbackend.bean.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserUtil {
    
    /**
     * 将User对象转换为前端所需的Map格式
     * @param user 用户对象
     * @return 包含用户基本信息的Map
     */
    public static Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> userMap = new LinkedHashMap<>();
        if (user != null) {
            userMap.put("userId", user.getId());
            userMap.put("userName", user.getUsername());
            userMap.put("avatar", UrlUtil.getUrl(user.getAvatarUrl()));
            userMap.put("bio", user.getBio());
            userMap.put("role", user.getRole());
            userMap.put("gender", user.getGender());
        }
        return userMap;
    }

    /**
     * 将User对象列表转换为前端所需的Map列表格式
     * @param users 用户对象列表
     * @return 包含用户基本信息的Map列表
     */
    public static List<Map<String, Object>> convertUsersToMapList(List<User> users) {
        List<Map<String, Object>> userMapList = new ArrayList<>();
        if (users != null) {
            for (User user : users) {
                userMapList.add(convertUserToMap(user));
            }
        }
        return userMapList;
    }
}