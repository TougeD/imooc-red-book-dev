package com.imooc.handler;

import com.imooc.exceptions.BaseException;
import com.imooc.exceptions.FileUploadErrorException;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 捕获业务异常
     * @return
     */
    @ExceptionHandler({BaseException.class})
    public GraceJSONResult exceptionHandler(BaseException ex){
        log.error("异常信息：{}",ex.getMessage());
        return GraceJSONResult.exception(ex.getResponseStatusEnum());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public GraceJSONResult maxUploadSizeExceededException(MaxUploadSizeExceededException ex){
        log.error("异常信息：文件太大了，{},",ex.getMessage());
        throw new FileUploadErrorException(ResponseStatusEnum.FILE_UPLOAD_FAILD);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public GraceJSONResult validException(MethodArgumentNotValidException e){
        BindingResult result = e.getBindingResult();
        //判断BindingResult中是否保存了错误的验证信息 如果有 则需要返回给前端
        Map<String, String> errors = getErrors(result);
        return GraceJSONResult.errorMap(errors);
    }

    public Map<String, String> getErrors(BindingResult result){
        List<FieldError> errorList = result.getFieldErrors();
        Map<String, String> map = new HashMap<>();
        for(FieldError fieldError : errorList){
            //错误所对应的属性字段名
            String field = fieldError.getField();
            //错误的信息
            String defaultMessage = fieldError.getDefaultMessage();
            map.put(field,defaultMessage);
        }
        return map;
    }
}
