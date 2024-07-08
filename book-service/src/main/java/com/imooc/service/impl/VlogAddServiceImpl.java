package com.imooc.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.mapper.VlogMapper;
import com.imooc.pojo.Vlog;
import com.imooc.service.VlogAddService;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@Service
public class VlogAddServiceImpl extends BaseInfoProperties implements VlogAddService {

    @Autowired
    private VlogMapper vlogMapper;

    /**
     * 查询用户的公开/私密的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @param yesOrNo
     * @return
     */
    @Override
    public PagedGridResult queryMyVlogList(String userId, Integer page, Integer pageSize, Integer yesOrNo) {
//        Example example = new Example(Vlog.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("vlogerId",userId);
//        criteria.andEqualTo("isPrivate",yesOrNo);
//        PageHelper.startPage(page,pageSize);
//
//        List<Vlog> vlogs = vlogMapper.selectByExample(example);
        PageHelper.startPage(page,pageSize);
        Vlog vlog = new Vlog();
        vlog.setVlogerId(userId);
        vlog.setIsPrivate(yesOrNo);
        Page<Vlog> vlogPage = vlogMapper.queryBatchByVlog(vlog);
        List<Vlog> result = vlogPage.getResult();

        return setterPagedGrid(result,page);
    }
}
