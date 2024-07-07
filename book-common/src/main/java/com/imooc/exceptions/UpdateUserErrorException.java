package com.imooc.exceptions;

import com.imooc.grace.result.ResponseStatusEnum;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */
public class UpdateUserErrorException extends BaseException{

    public UpdateUserErrorException() {
    }

    public UpdateUserErrorException(ResponseStatusEnum responseStatusEnum) {
        super(responseStatusEnum);
    }
}
