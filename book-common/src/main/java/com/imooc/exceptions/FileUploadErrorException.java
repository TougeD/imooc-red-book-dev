package com.imooc.exceptions;

import com.imooc.grace.result.ResponseStatusEnum;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */
public class FileUploadErrorException extends BaseException{

    public FileUploadErrorException() {
    }

    public FileUploadErrorException(ResponseStatusEnum responseStatusEnum) {
        super(responseStatusEnum);
    }
}
