package com.imooc.aspect;

import com.imooc.annotation.AutoFill;
import com.imooc.constant.AutoFillConstant;
import com.imooc.enums.OperationType;
import com.imooc.pojo.Users;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* com.imooc.mapper.*.*(..)) && @annotation(com.imooc.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知 在通知中进行公共字段的赋值
     * 匹配切入点autoFillPointCut()的Pointcut 则执行此方法
     */
    @Before(value = "autoFillPointCut()")
    public void before(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充");
        //获取当前被拦截的方法上的数据库操作类型
        //方法签名对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获得方法上的注解对象
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        //得到对应的数据库类型
        OperationType value = annotation.value();

        //获取到当前被拦截方法的第一参数 实体对象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }
        Date now = new Date();
        //获得输入参数的第一个参数
        Object entity = args[0];
        if(value == OperationType.INSERT){
            //说明是插入语句
            try {
                Method setCreateTime = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_TIME, Date.class);
                Method setUpdateTime = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, Date.class);

                setCreateTime.invoke(entity,now);
                setUpdateTime.invoke(entity,now);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {    //更新数据库操作
            try {

                Method setUpdateTime = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, Date.class);
                setUpdateTime.invoke(entity,now);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
