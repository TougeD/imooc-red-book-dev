package com.imooc.controller;

import com.imooc.VO.UsersVO;
import com.imooc.dto.LoginDTO;
import com.imooc.exceptions.LoginErrorException;
import com.imooc.exceptions.SMSSendErrorException;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Users;
import com.imooc.service.UserService;
import com.imooc.utils.IPUtil;
import com.imooc.utils.SMSUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */
@RestController
@Api(tags = "通行证接口模块")
@Slf4j
@RequestMapping("passport")
public class PassportController extends BaseInfoProperties {

    @Autowired
    private SMSUtils smsUtils;

    @Autowired
    private UserService userService;

    @PostMapping("logout")
    @ApiOperation(value = "注销用户接口")
    public GraceJSONResult logout(@RequestParam(value = "userId") String userId,
                                  HttpServletRequest request) throws Exception{
        //后端只需要清除用户的token信息即可 前端也需要清除 清除本地app中的用户信息和token会话信息
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
        return GraceJSONResult.ok();
    }

    @PostMapping("login")
    @ApiOperation(value = "登录接口")
    public GraceJSONResult login(@Valid @RequestBody LoginDTO loginDTO,
//                                 BindingResult result,
                                 HttpServletRequest request) throws Exception {
//        //判断BindingResult中是否保存了错误的验证信息 如果有 则需要返回给前端
//        if(result.hasErrors()){
//            Map<String, String> errors = getErrors(result);
//            return GraceJSONResult.errorMap(errors);
//        }
        String mobile = loginDTO.getMobile();
        String smsCode = loginDTO.getSmsCode();

        //1. 从redis中获得验证码进行校验 是否匹配
        String key = MOBILE_SMSCODE + ":" + loginDTO.getMobile();
        String redisCode = redisOperator.get(key);

        if (StringUtils.isBlank(redisCode)) {
            //如果验证码的key不存在
            throw new LoginErrorException(ResponseStatusEnum.SMS_IS_NOT_EXIST);
        } else if (!redisCode.equalsIgnoreCase(smsCode)) {
            //如果验证码与发送的验证码不匹配
            throw new LoginErrorException(ResponseStatusEnum.SMS_IS_NOT_RIGHT);
        }

        //2. 查询数据库 判断用户是否存在
        Users user = userService.queryMobileIsExist(mobile);
        if (user == null) {
            //用户没有注册过 则为null  需要注册信息入库
            user = userService.createUser(mobile);
        }

        //3. 如果不为空 继续下方业务 可以保存用户信息和会话信息  其实就是保存了token
        String uToken = UUID.randomUUID().toString();
        redisOperator.set(REDIS_USER_TOKEN + "" + user.getId(), uToken);

        //4. 用户登录注册成功以后 删除redis中的短信用户码
        redisOperator.del(key);

        //5. 返回用户信息 包含token令牌
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserToken(uToken);

        return GraceJSONResult.ok(usersVO);

        /**
         if (StringUtils.isBlank(mobile) || StringUtils.isBlank(smsCode)) {
         throw new LoginErrorException(ResponseStatusEnum.LOGIN_NULL_FAILED);
         } else if (StringUtils.length(mobile) != 11) {
         //如果手机号不为11位
         throw new SMSSendErrorException(ResponseStatusEnum.MOBILE_LENGTH_ERROR);
         }
         String key = MOBILE_SMSCODE + ":" + loginVO.getMobile();
         if (!redisOperator.keyIsExist(key)) {
         //如果验证码的key不存在
         throw new LoginErrorException(ResponseStatusEnum.SMS_IS_NOT_EXIST);
         }

         //验证码存在时
         if(!redisOperator.get(key).equals(smsCode)){
         //如果验证码与发送的验证码不匹配
         throw new LoginErrorException(ResponseStatusEnum.SMS_IS_NOT_RIGHT);
         }
         */
        //验证码匹配
//        return GraceJSONResult.ok("登录成功");
    }

    @PostMapping("getSMSCode")
    @ApiOperation(value = "获取短信验证码接口")
    public GraceJSONResult getSMSCode(@RequestParam String mobile, HttpServletRequest request) throws Exception {
        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.ok();
        }
        if (StringUtils.length(mobile) != 11) {
            //如果手机号不为11位
            throw new SMSSendErrorException(ResponseStatusEnum.MOBILE_LENGTH_ERROR);
        }
        //获得用户ip
        String userIp = IPUtil.getRequestIp(request);
        //根据用户ip进行限制  限制用户在60秒之内只能获得一次验证码
        //如果这个用户在60s内发送过验证码 这个key才会存在 顶多存在60s后消失
        redisOperator.setnx60s(MOBILE_SMSCODE + ":" + userIp, userIp);

        String code = (int) ((Math.random() * 9 + 1) * 100000) + "";
        smsUtils.sendSMS(mobile, code);
        //将验证码放入到redis中 用于后续验证
        redisOperator.set(MOBILE_SMSCODE + ":" + mobile, code, 5 * 60);

        //把验证码放入到redis中 用于后续的验证
        return GraceJSONResult.ok();
    }

}
