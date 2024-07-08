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
    IndexVlogVO queryVlogDetailById(String vlogId);

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
    PagedGridResult getIndexVlogList(String search, Integer page, Integer pageSize);
}
