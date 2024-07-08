package com.imooc.interceptor;

import com.imooc.base.BaseInfoProperties;
import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */
@Component
@Slf4j
public class UserTokenInterceptor extends BaseInfoProperties implements HandlerInterceptor {

    @Autowired
    private RedisOperator redisOperator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //从header中获取用户id和token
        String userId = request.getHeader("headerUserId");
        String headerUserToken = request.getHeader("headerUserToken");
        String key = REDIS_USER_TOKEN + "" + userId;
        log.info("{}",key);
        String uToken = redisOperator.get(key);
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(headerUserToken) || uToken == null) {
            //如果本地没有对应的token 或者传过来的token和本地的token不一致
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            log.info("触发令牌检验拦截器，校验不通过");
            return false;
        }else {
            if (!StringUtils.equalsIgnoreCase(uToken, headerUserToken)) {
                //说明别的手机登录了此号码 导致令牌变了
                GraceException.display(ResponseStatusEnum.TICKET_INVALID);
                log.info("触发令牌检验拦截器，校验不通过");
                return false;
            }
        }
        log.info("触发令牌检验拦截器，校验通过");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
