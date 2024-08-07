package com.imooc.interceptor;

import com.imooc.base.BaseInfoProperties;
import com.imooc.exceptions.SMSSendErrorException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
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
public class PassportInterceptor extends BaseInfoProperties implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("触发短信拦截器");
        //获得用户ip
        String userIp = IPUtil.getRequestIp(request);

        if (redisOperator.keyIsExist(MOBILE_SMSCODE + ":" + userIp)) {
            //如果key还是存在 则说明还在验证阶段
//            log.info("短信发送频率太大！");
            //return false;
            throw new SMSSendErrorException(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
        }
        log.info("通过短信拦截");
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
