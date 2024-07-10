package com.imooc.exceptions;

import com.imooc.grace.result.ResponseStatusEnum;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */
public class FansFollowErrorException extends BaseException{

    public FansFollowErrorException() {
    }

    public FansFollowErrorException(ResponseStatusEnum responseStatusEnum) {
        super(responseStatusEnum);
    }
}
