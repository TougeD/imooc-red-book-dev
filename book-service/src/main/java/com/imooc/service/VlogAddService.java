package com.imooc.service;

import com.imooc.utils.PagedGridResult;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */
public interface VlogAddService {


    public PagedGridResult queryMyVlogList(String userId, Integer page, Integer pageSize, Integer yesOrNo);
}
