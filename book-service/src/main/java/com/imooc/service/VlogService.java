package com.imooc.service;

import com.imooc.dto.VlogDTO;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.IndexVlogVO;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */
public interface VlogService {

    /**
     * 把Counts输入数据库
     */
    public void flushCounts(String volgId, Integer counts);

    /**
     * 查询朋友发布的短视频列表
     *
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult gerMyFriendVlogList(String userId, Integer page, Integer pageSize);

    /**
     * 查询用户关注的博主发布的短视频列表
     *
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult gerMyFollowVlogList(String userId, Integer page, Integer pageSize);

    /**
     * 查询用户点赞过的短视频
     *
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult gerMyLikedVlogList(String userId, Integer page, Integer pageSize);

    /**
     * 获得用户点赞视频的总数
     *
     * @param vlogId
     * @return
     */
    public Integer getVlogBeLikedCounts(String vlogId);

    /**
     * 用户点赞/喜欢的视频
     *
     * @param userId
     * @param vlogId
     */
    public void userLikeVlog(String userId, String vlogId);

//    /**
//     * 查询用户的公开/私密的视频列表
//     *
//     * @param userId
//     * @param page
//     * @param pageSize
//     * @param yesOrNo
//     * @return
//     */
//    PagedGridResult queryMyVlogList(String userId, Integer page, Integer pageSize, Integer yesOrNo);

    /**
     * 改变用户视频可见状态
     *
     * @param userId
     * @param vlogId
     * @param yesOrNo
     */
    void changeToPrivateOrPublic(String userId, String vlogId, Integer yesOrNo);

    /**
     * @param vlogId
     */
    IndexVlogVO queryVlogDetailById(String userId, String vlogId);

    /**
     * 新增视频
     *
     * @param vlogDTO
     */
    void createVlog(VlogDTO vlogDTO);

    /**
     * 查询首页/搜索的vlog列表
     *
     * @param search
     * @return
     */
    PagedGridResult getIndexVlogList(String search, String userId, Integer page, Integer pageSize);

    /**
     * 取消点赞视频
     *
     * @param userId
     * @param vlogId
     */
    void userUnLikeVlog(String userId, String vlogId);
}
