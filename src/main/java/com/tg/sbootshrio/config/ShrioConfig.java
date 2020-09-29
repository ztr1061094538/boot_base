package com.tg.sbootshrio.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShrioConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource(){
        return new DruidDataSource();
    }

    //filte
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("manager") DefaultWebSecurityManager manager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(manager);

        //设置shiro 内置过滤器

        Map<String, String> filterMa=new LinkedHashMap<String, String>();

        //先注释  放开权限
//        filterMa.put("/add","authc") ;
//        filterMa.put("/update","authc") ;
        shiroFilterFactoryBean.setLoginUrl("/tologin");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMa);
        return shiroFilterFactoryBean;
    }

    //manage
    @Bean(name = "manager")
    public DefaultWebSecurityManager getManage(@Qualifier("myRealml") MyRealml myRealml) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        //关联 realm
        manager.setRealm(myRealml);
        return manager;
    }

    //realm  需要自定义
    @Bean(name = "myRealml")
    public MyRealml myRealml() {
        return new MyRealml();
    }


}
