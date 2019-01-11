package com.sunny.dao.impl;

import com.sunny.dao.UserDao;
import com.sunny.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class UserDaoImpl implements UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User getUserByUserName(String userName) {
        String sql = "SELECT username,password FROM sys_user WHERE username=?";
        List<User> list = jdbcTemplate.query(sql, new String[]{userName}, new RowMapper<User>() {
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setUserName(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                return user;
            }
        });
        if (!CollectionUtils.isEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    public List<String> getRolesByUserName(String userName) {
        String sql = "SELECT R.`name` FROM sys_user_role UR " +
                     "LEFT JOIN sys_user U ON UR.sys_user_id = U.id " +
                     "LEFT JOIN sys_role R ON UR.sys_role_id = R.id " +
                     "WHERE U.username = ?";
        List<String> roles = jdbcTemplate.query(sql, new String[]{userName}, new RowMapper<String>() {
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("name");
            }
        });
        return roles;
    }

    public List<String> getPermissionByUserName(String userName) {
        String sql = "SELECT P.`name` FROM sys_role_permission RP " +
                     "LEFT JOIN " +
                     "(" +
                     " SELECT R.id FROM sys_user_role UR " +
                     " LEFT JOIN sys_user U ON UR.sys_user_id = U.id " +
                     " LEFT JOIN sys_role R ON UR.sys_role_id = R.id " +
                     " WHERE U.username = ?" +
                     " ) T" +
                     " ON T.id = RP.sys_role_id " +
                     " LEFT JOIN sys_permission P ON RP.sys_permission_id = P.id ";
        List<String> permissions = jdbcTemplate.query(sql, new String[]{userName}, new RowMapper<String>() {
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("name");
            }
        });
        return permissions;
    }
}
