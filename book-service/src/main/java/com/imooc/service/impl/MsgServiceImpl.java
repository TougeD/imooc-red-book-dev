package com.imooc.service.impl;

import com.imooc.base.BaseInfoProperties;
import com.imooc.enums.MessageEnum;
import com.imooc.mo.MessageMO;
import com.imooc.pojo.Users;
import com.imooc.repository.MessageRepository;
import com.imooc.service.MsgService;
import com.imooc.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@Service
public class MsgServiceImpl extends BaseInfoProperties implements MsgService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    /**
     * 查询消息列表
     *
     * @param toUserId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<MessageMO> queryList(String toUserId, Integer page, Integer pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createTime");
        List<MessageMO> list = messageRepository.findAllByToUserIdEqualsOrderByCreateTimeDesc(toUserId, pageable);
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
        return list;
    }

    /**
     * 创建消息
     *
     * @param fromUserId
     * @param toUserId
     * @param type
     * @param msgContent
     */
    @Override
    public void createMsg(String fromUserId, String toUserId, Integer type, Map msgContent) {

        Users fromUser = userService.getUserById(fromUserId);

        MessageMO messageMO = new MessageMO();
        messageMO.setFromUserId(fromUserId);
        messageMO.setFromNickname(fromUser.getNickname());
        messageMO.setFromFace(fromUser.getFace());

        messageMO.setToUserId(toUserId);

        if (msgContent != null) {
            messageMO.setMsgContent(msgContent);
        }
        messageMO.setMsgType(type);

        messageMO.setCreateTime(new Date());

        messageRepository.save(messageMO);
    }


}
