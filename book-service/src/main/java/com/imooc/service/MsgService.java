package com.imooc.service;

import com.imooc.mo.MessageMO;

import java.util.List;
import java.util.Map;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */
public interface MsgService {

    /**
     * 创建消息
     */
    public void createMsg(String fromUserId, String toUserId, Integer type, Map msgContent);

    /**
     * 查询消息列表
     * @param toUserId
     * @param page
     * @param pageSize
     * @return
     */
    public List<MessageMO> queryList(String toUserId,Integer page,Integer pageSize);
}
