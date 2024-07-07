package com.imooc.exceptions;

import com.imooc.grace.result.ResponseStatusEnum;
import lombok.Data;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@Data
public class SMSSendErrorException extends BaseException{

    public SMSSendErrorException(ResponseStatusEnum responseStatusEnum) {
        super(responseStatusEnum);
    }
}
