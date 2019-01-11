package com.sunny.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

//AuthorizationFilter为授权相关filter
public class RoleOrFilter extends AuthorizationFilter {
    protected boolean isAccessAllowed(javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse, Object o) throws Exception {
        //从父类AuthorizationFilter中获取当前的subject对象
        Subject subject = getSubject(servletRequest,servletResponse);
        String[] roles = (String[]) o;//此处o为在配置过滤器链filterChainDefinitions时，中括号中的roles数据
        if (roles == null || roles.length==0){//说明不需要任何角色、权限
            return true;
        }else {
            for(String role:roles){
                if (role.equals("admin")){
                    return true;
                }
            }
        }
        return false;
    }
}
