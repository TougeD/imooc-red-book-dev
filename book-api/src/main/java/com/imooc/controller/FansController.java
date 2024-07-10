package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.exceptions.FansFollowErrorException;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Users;
import com.imooc.service.FansService;
import com.imooc.service.UserService;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */
@RestController
@Slf4j
@Api(tags = "粉丝相关业务功能的接口")
@RequestMapping("fans")
public class FansController extends BaseInfoProperties {

    @Autowired
    private FansService fansService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("queryMyFans")
    @ApiOperation(value = "查询我的粉丝")
    public GraceJSONResult queryMyFans(@RequestParam(required = true) String myId,
                                          @RequestParam(required = false,defaultValue = "1") Integer page,
                                          @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        return GraceJSONResult.ok(fansService.queryMyFans(myId,page,pageSize));
    }

    @GetMapping("queryMyFollows")
    @ApiOperation(value = "查询我的关注")
    public GraceJSONResult queryMyFollows(@RequestParam(required = true) String myId,
                                          @RequestParam(required = false,defaultValue = "1") Integer page,
                                          @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        return GraceJSONResult.ok(fansService.queryMyFollows(myId,page,pageSize));
    }

    @GetMapping("queryDoIFollowVloger")
    @ApiOperation(value = "查询是否关注")
    public GraceJSONResult queryDoIFollowVloger(@RequestParam(required = true) String myId,
                                                @RequestParam(required = true) String vlogerId){

//        if(redisOperator.keyIsExist(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + vlogerId)){
//            return GraceJSONResult.ok(true);
//        }
//        return GraceJSONResult.ok(false);
        return GraceJSONResult.ok(fansService.queryDoIFollowVloger(myId,vlogerId));
    }

    @PostMapping("cancel")
    @ApiOperation("取消关注")
    public GraceJSONResult cancel(@RequestParam(required = true) String myId,
                                  @RequestParam(required = true) String vlogerId){
        //删除业务的执行
        fansService.doCancel(myId,vlogerId);

        //博主的粉丝-1 我的关注-1
        redisOperator.decrement(REDIS_MY_FOLLOWS_COUNTS + ":" + myId, 1);
        redisOperator.decrement(REDIS_MY_FANS_COUNTS + ":" + vlogerId, 1);

        //我和博主的关联关系，依赖redis 不要存储数据库，避免db的性能瓶颈
        redisOperator.del(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + vlogerId);

        return GraceJSONResult.ok();
    }

    @PostMapping("follow")
    @ApiOperation("关注接口")
    public GraceJSONResult follow(@RequestParam(required = true) String myId,
                                  @RequestParam(required = true) String vlogerId) {
        //判断当前用户 自己不能关注自己
        if (myId.equalsIgnoreCase(vlogerId)) {
            throw new FansFollowErrorException(ResponseStatusEnum.FANS_FOLLOW_SELF_ERROR);
        }

        //判断两个id对应的用户是否存在
        Users vloger = userService.getUserById(vlogerId);
        Users myInfo = userService.getUserById(myId);

        if (vloger == null || myInfo == null) {
            throw new FansFollowErrorException(ResponseStatusEnum.FANS_FOLLOW_NOT_EXIST_ERROR);
        }

        //保存分析关系到数据库
        fansService.doFollow(myId, vlogerId);

        //博主的粉丝+1 我的关注+1
        redisOperator.increment(REDIS_MY_FOLLOWS_COUNTS + ":" + myId, 1);
        redisOperator.increment(REDIS_MY_FANS_COUNTS + ":" + vlogerId, 1);

        //我和博主的关联关系，依赖redis 不要存储数据库，避免db的性能瓶颈
        redisOperator.set(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + vlogerId, "1");


        return GraceJSONResult.ok();
    }
}
