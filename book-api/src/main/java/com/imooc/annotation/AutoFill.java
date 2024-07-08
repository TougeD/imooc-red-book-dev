package com.imooc.annotation;

import com.imooc.enums.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api 自定义注解 用于标识某个方法需要进行功能字段自动填充处理
 * @since 2024年 月 日
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface AutoFill {
    OperationType value();
}
