package com.imooc.annotation;

import com.imooc.enums.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@Target(ElementType.METHOD) //制定这个注解只能加在方法上
@Retention(RetentionPolicy.RUNTIME) //固定的写法
public @interface AutoFill {

    OperationType value();
}
