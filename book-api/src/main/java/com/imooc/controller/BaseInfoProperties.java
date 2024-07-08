//package com.imooc.controller;
//
//import com.github.pagehelper.PageInfo;
//import com.imooc.utils.PagedGridResult;
//import com.imooc.utils.RedisOperator;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author 小小低头哥
// * @version v1.0
// * @api
// * @since 2024年 月 日
// */
//@RestController
//@Slf4j
//public class BaseInfoProperties {
//
//    @Autowired
//    public RedisOperator redisOperator;
//
//    public static final Integer COMMON_START_PAGE = 1;
//    public static final Integer COMMON_PAGE_SIZE = 10;
//
//    public static final String MOBILE_SMSCODE = "mobile:smscode";
//    public static final String REDIS_USER_TOKEN = "redis_user_token";
//    public static final String REDIS_USER_INFO = "redis_user_info";
//
//    // 我的关注总数
//    public static final String REDIS_MY_FOLLOWS_COUNTS = "redis_my_follows_counts";
//    // 我的粉丝总数
//    public static final String REDIS_MY_FANS_COUNTS = "redis_my_fans_counts";
//
//    // 视频和发布者获赞数
//    public static final String REDIS_VLOG_BE_LIKED_COUNTS = "redis_vlog_be_liked_counts";
//    public static final String REDIS_VLOGER_BE_LIKED_COUNTS = "redis_vloger_be_liked_counts";
//
//    public PagedGridResult setterPagedGrid(List<?> list,Integer page){
//
//        PageInfo<?> pageInfo = new PageInfo<>(list);
//        PagedGridResult pagedGridResult = new PagedGridResult();
//        pagedGridResult.setRows(list);
//        pagedGridResult.setPage(page);
//        pagedGridResult.setRecords(pageInfo.getTotal());
//        pagedGridResult.setTotal(pageInfo.getPages());
//        return pagedGridResult;
//    }
//
////    public Map<String, String> getErrors(BindingResult result){
////        List<FieldError> errorList = result.getFieldErrors();
////        Map<String, String> map = new HashMap<>();
////        for(FieldError fieldError : errorList){
////            //错误所对应的属性字段名
////            String field = fieldError.getField();
////            //错误的信息
////            String defaultMessage = fieldError.getDefaultMessage();
////            map.put(field,defaultMessage);
////        }
////        return map;
////    }
//
//}
