package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.enums.MessageEnum;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.mo.MessageMO;
import com.imooc.repository.MessageRepository;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@RestController
@Api(tags = "消息模块")
@Slf4j
@RequestMapping("msg")
public class MsgController2 extends BaseInfoProperties {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("list")
    public GraceJSONResult list(@RequestParam String userId,
                                @RequestParam(required = false,defaultValue = "0") Integer page,
                                @RequestParam(required = false,defaultValue = "10") Integer pageSize){

        //Mongodb 从0分页 区别于数据库
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createTime");
        List<MessageMO> list = messageRepository.findAllByToUserIdEqualsOrderByCreateTimeDesc(userId, pageable);
        for (MessageMO msg : list) {
            //如果类型是关注消息 则需要查询我之前有没有关注过他 用于在前端标记“互粉”“互关”
            if(msg.getMsgType() != null && msg.getMsgType() == MessageEnum.FOLLOW_YOU.type){
                Map msgContent = msg.getMsgContent();
                if(msgContent == null){
                    msgContent = new HashMap();
                }
                String relationship = redisOperator.get(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + msg.getToUserId() + ":" + msg.getFromUserId());
                if(StringUtils.isNotBlank(relationship) && relationship.equalsIgnoreCase("1")){
                    msgContent.put("isFriend",true);
                }else {
                    msgContent.put("isFriend",false);
                }
                msg.setMsgContent(msgContent);
            }
        }


        return GraceJSONResult.ok(list);
    }
}
