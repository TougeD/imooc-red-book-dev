package com.imooc.exceptions;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

import com.imooc.grace.result.ResponseStatusEnum;
import lombok.Data;

/**
 * 业务异常
 */
@Data
public class BaseException extends RuntimeException{

    private ResponseStatusEnum responseStatusEnum ;

    public BaseException() {
    }

    public BaseException(ResponseStatusEnum responseStatusEnum) {
        super(responseStatusEnum.msg());
        this.responseStatusEnum = responseStatusEnum;
    }
}
