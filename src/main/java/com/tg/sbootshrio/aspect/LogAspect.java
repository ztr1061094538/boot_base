package com.tg.sbootshrio.aspect;

import com.alibaba.fastjson.JSON;
import com.tg.sbootshrio.annotation.MyLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.genid.GenId;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class LogAspect {

    /**
     * 配置切入点  配置为自定义注解
     */
    @Pointcut("@annotation(com.tg.sbootshrio.annotation.MyLog)")
    public void logPointCut() {
    }

    /**
     * 环绕通知
     */
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        //执行方法
        Object proceed = joinPoint.proceed();
        long time = System.currentTimeMillis() - startTime;

        try {
            saveLog(joinPoint, time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proceed;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long time) {

        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        //获取控制器  包含包名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();//方法名
        Object[] args = joinPoint.getArgs();
        String params = "";
        if (args.length > 0) {
            params = JSON.toJSONString(args);
        }
        //打印该方法的时间
        log.info("请求{}.{}耗时{}毫秒", className, methodName, time);
        //请求com.tg.sbootshrio.controller.ShrioController.toIndex耗时3毫秒

        //获取自定义注解的字段值
        MyLog myLog = signature.getMethod().getAnnotation(MyLog.class);
        if (myLog != null) {
            log.info("模块={}，动作={},耗时{}毫秒", myLog.title(), myLog.action(), time);
            //模块=去登陆模块，动作=去登陆、分页、查询,耗时3毫秒
        }
        log.info("接口地址:{},请求方式:{},入参:{}",request.getRequestURL(),request.getMethod(),params);
        //接口地址:http://localhost:8070/toindex,请求方式:GET,入参:[{}]

    }

}
