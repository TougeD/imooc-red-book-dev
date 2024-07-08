package com.imooc.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.dto.VlogDTO;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.VlogMapper;
import com.imooc.pojo.Vlog;
import com.imooc.service.VlogService;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.IndexVlogVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class VlogServiceImpl extends BaseInfoProperties implements VlogService {

    @Autowired
    private VlogMapper vlogMapper;

    @Autowired
    private Sid sid;


    /**
     * 改变用户视频可见状态
     * @param userId
     * @param vlogId
     * @param yesOrNo
     */
    @Transactional
    @Override
    public void changeToPrivateOrPublic(String userId, String vlogId, Integer yesOrNo) {
        Vlog vlog = new Vlog();
        vlog.setIsPrivate(yesOrNo);
        vlog.setId(vlogId);
        vlog.setVlogerId(userId);
        vlogMapper.updateByVlog(vlog);
    }

    @Override
    public IndexVlogVO queryVlogDetailById(String vlogId) {
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isNotBlank(vlogId)){
            map.put("vlogId",vlogId);
        }
        List<IndexVlogVO> result = vlogMapper.queryVlogDetailById(map);
        if(result != null && result.size() >0 ){
            return result.get(0);
        }
        return null;
    }

    /**
     * 查询首页/搜索的vlog列表
     * @param search
     * @return
     */
    @Override
    public PagedGridResult getIndexVlogList(String search, Integer page, Integer pageSize) {
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isNotBlank(search)){
            map.put("search",search);
        }
        PageHelper.startPage(page,pageSize);
        Page<IndexVlogVO> listPage = vlogMapper.queryBatch(map);
        List<IndexVlogVO> result = listPage.getResult();
        return setterPagedGrid(result,page);
    }

    /**
     * 新增视频
     * @param vlogDTO
     */
    @Transactional
    @Override
    public void createVlog(VlogDTO vlogDTO){
        String vid = sid.nextShort();
        Vlog vlog = new Vlog();
        BeanUtils.copyProperties(vlogDTO,vlog);
        vlog.setId(vid);
        vlog.setLikeCounts(0);
        vlog.setCommentsCounts(0);
        vlog.setIsPrivate(YesOrNo.NO.type);
//        vlog.setCreatedTime(new Date());
//        vlog.setUpdatedTime(new Date());

        vlogMapper.add(vlog);
//        vlogMapper.insert(vlog);
    }

    @Override
    public String toString() {
        return null;
    }
}
