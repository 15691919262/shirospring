package com.sunny.customRealm;

import com.sunny.dao.UserDao;
import com.sunny.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class MySecurityRealm extends AuthorizingRealm {

    @Autowired
    private UserDao userDao;

    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 1.从主体传过来的认证信息中，获取用户名
        String userName = (String) authenticationToken.getPrincipal();

        // 2.通过用户名获取密码
        String password = getPasswordByUserName(userName);
        if (StringUtils.isEmpty(password)){
            return null;
        }

        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(userName,password,"myRealm");
        simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(userName));
        return simpleAuthenticationInfo;
    }

    /**
     * 从数据库中获取密码
     * @param userName
     * @return
     */
    private String getPasswordByUserName(String userName) {
        User user = userDao.getUserByUserName(userName);
        if (user!=null){
            return user.getPassword();
        }
        return null;
    }


    /**
     * 授权
     * @param principalCollection
     * @return
     */
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("从MySQL中获取授权数据。。");
        String userName = (String) principalCollection.getPrimaryPrincipal();
        //角色信息
        Set<String> roleSet = getRolesByUserName(userName);
        //权限信息
        Set<String> permissionSet = getPermissionByUserName(userName);

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(roleSet);
        simpleAuthorizationInfo.setStringPermissions(permissionSet);
        return simpleAuthorizationInfo;
    }


    /**
     * 从数据库中获取用户角色
     * @param userName
     * @return
     */
    private Set<String> getRolesByUserName(String userName) {
        List<String> roles = userDao.getRolesByUserName(userName);
        Set<String> roleSet = new HashSet<String>(roles);
        return roleSet;
    }

    /**
     * 从数据库中获取用户权限
     * @param userName
     * @return
     */
    private Set<String> getPermissionByUserName(String userName) {
        List<String> permissions = userDao.getPermissionByUserName(userName);
        Set<String> permissionSet = new HashSet<String>(permissions);
        return permissionSet;
    }

    public static void main(String[] args) {
        Md5Hash md5Hash = new Md5Hash("111222","ranger");
        System.out.println(md5Hash.toString());
    }


}
