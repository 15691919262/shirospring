package com.sunny.controller;

import com.sunny.vo.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @RequestMapping(value = "/userLogin",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String login(User user){
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUserName(),user.getPassword());
        Subject subject = SecurityUtils.getSubject();
        try {
            token.setRememberMe(user.isRememberMe());
            subject.login(token);
        } catch (AuthenticationException e) {
            return e.getMessage();
        }
        if (subject.hasRole("admin")){
            return "有admin角色";
        }else {
            return "无admin角色";
        }
    }

    /*************************************测试注解接口开始******************************************************/
    @RequiresRoles("admin")//当前主体访问此接口必须具备admin角色
    @RequestMapping(value = "/testRole",method = RequestMethod.GET)
    @ResponseBody
    public String testRole(){
        return "has admin role!";
    }

    @RequiresPermissions("user:delete")//当前主体访问此接口必须具备user:deleteAll权限
    @RequestMapping(value = "/testPermission",method = RequestMethod.GET)
    @ResponseBody
    public String testPermission(){
        return "has deleteAll permission!";
    }
    /*************************************测试注解接口结束******************************************************/


    /*************************************测试shiro内置过滤器接口开始******************************************************/
    @RequestMapping(value = "/testRole1",method = RequestMethod.GET)//有权限
    @ResponseBody
    public String testRole1(){
        return "has admin role!";
    }
    @RequestMapping(value = "/testRole2",method = RequestMethod.GET)//无权限，跳转403.html
    @ResponseBody
    public String testRole2(){
        return "has admin admin admin1 role!";
    }

    @RequestMapping(value = "/testPermission1",method = RequestMethod.GET)//有权限
    @ResponseBody
    public String testPermission1(){
        return "has delete permission!";
    }
    @RequestMapping(value = "/testPermission2",method = RequestMethod.GET)//无权限，跳转403.html
    @ResponseBody
    public String testPermission2(){
        return "has delete and deleteAll permission!";
    }
    /*************************************测试shiro内置过滤器接口结束******************************************************/
    @RequestMapping(value = "/testRoleOr",method = RequestMethod.GET)//有权限，响应成功
    @ResponseBody
    public String testRoleOr(){
        return "contains admin role!";
    }


}
