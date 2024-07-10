package com.imooc.service;

import com.imooc.utils.PagedGridResult;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */
public interface FansService {

    /**
     * 查询我的粉丝列表
     * @param myId
     * @param page
     * @param pageSize
     * @return
     */
    PagedGridResult queryMyFans(String myId,Integer page,Integer pageSize);

    /**
     * 查询我的关注的博主列表
     * @param myId
     * @param page
     * @param pageSize
     * @return
     */
    PagedGridResult queryMyFollows(String myId,Integer page,Integer pageSize);

    /**
     * 查询用户是否关注博主
     * @param myId
     * @param vlogerId
     * @return
     */
    public boolean queryDoIFollowVloger(String myId, String vlogerId);

    /**
     * 关注
     * @param myId
     * @param vlogerId
     */
    public void doFollow(String myId, String vlogerId);

    /**
     * 取消关注
     * @param myId
     * @param vlogerId
     */
    public void doCancel(String myId,String vlogerId);
}
