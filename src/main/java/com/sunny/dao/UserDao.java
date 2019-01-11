package com.sunny.dao;

import com.sunny.vo.User;

import java.util.List;

public interface UserDao {
    // 根据用户名获取用户
    User getUserByUserName(String userName);

    // 根据用户名获取角色
    List<String> getRolesByUserName(String userName);

    // 根据用户名获取权限
    List<String> getPermissionByUserName(String userName);
}
