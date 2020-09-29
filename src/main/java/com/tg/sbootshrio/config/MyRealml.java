package com.tg.sbootshrio.config;

import com.tg.sbootshrio.mapper.UserMapper;
import com.tg.sbootshrio.pojo.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 自定义得realm
 */
public class MyRealml extends AuthorizingRealm {
    @Autowired
    private UserMapper mapper;
    /**
     * 授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("执行授权    方法。");

        return null;
    }

    /**
     * 认证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
       //调用了 subject得登录方法才能入认证方法
        UsernamePasswordToken usertoken=(UsernamePasswordToken)token;
        String username = usertoken.getUsername();
        //伪造数据   实际上应该查  DB
        User user=new User();
        user.setUserName(username);
        List<User> select = mapper.select(user);
        if(select.isEmpty()){
            return null;
        }
        String password = select.get(0).getPassword();
        AuthenticationInfo authenticationInfo=new SimpleAuthenticationInfo(usertoken,password,"testrealm");
        return authenticationInfo;
    }
}
