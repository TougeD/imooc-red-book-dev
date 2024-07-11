package com.imooc.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.dto.VlogDTO;
import com.imooc.enums.MessageEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.MyLikedVlogMapper;
import com.imooc.mapper.VlogMapper;
import com.imooc.pojo.MyLikedVlog;
import com.imooc.pojo.Vlog;
import com.imooc.service.FansService;
import com.imooc.service.MsgService;
import com.imooc.service.VlogService;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.IndexVlogVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class VlogServiceImpl extends BaseInfoProperties implements VlogService {

    @Autowired
    private VlogMapper vlogMapper;

    @Autowired
    private MyLikedVlogMapper myLikedVlogMapper;

    @Autowired
    private FansService fansService;

    @Autowired
    private MsgService msgService;

    @Autowired
    private Sid sid;

    /**
     * 查询朋友发布的短视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PagedGridResult gerMyFriendVlogList(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("userId",userId);
        Page<IndexVlogVO> myFriendVlogList = vlogMapper.getMyFriendVlogList(map);
        List<IndexVlogVO> result = myFriendVlogList.getResult();

        for (IndexVlogVO v : result) {
            String vlogerId = v.getVlogerId();
            String vlogId = v.getVlogId();

            //判断用户是否点赞过视频
            if (StringUtils.isNotBlank(userId)) {
                //用户必定关注该博主
                boolean doIFollowVloger = fansService.queryDoIFollowVloger(userId, vlogerId);
                v.setDoIFollowVloger(doIFollowVloger);
                //判断用户是否点赞过视频
                v.setDoILikeThisVlog(doILikeVlog(userId, vlogId));
            }

            //获取当前视频被点赞过得总数
            v.setLikeCounts(getVlogBeLikedCounts(vlogId));

        }

        return setterPagedGrid(result,page);
    }

    private IndexVlogVO setterVo(IndexVlogVO v,String userId){
        String vlogerId = v.getVlogerId();
        String vlogId = v.getVlogId();

        //判断用户是否点赞过视频
        if (StringUtils.isNotBlank(userId)) {
            //用户必定关注该博主
            boolean doIFollowVloger = fansService.queryDoIFollowVloger(userId, vlogerId);
            v.setDoIFollowVloger(doIFollowVloger);
            //判断用户是否点赞过视频
            v.setDoILikeThisVlog(doILikeVlog(userId, vlogId));
        }

        //获取当前视频被点赞过得总数
        v.setLikeCounts(getVlogBeLikedCounts(vlogId));

        return v;
    }

    /**
     * 查询用户关注的博主发布的短视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PagedGridResult gerMyFollowVlogList(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("userId",userId);
        Page<IndexVlogVO> myFollowVlogList = vlogMapper.getMyFollowVlogList(map);
        List<IndexVlogVO> result = myFollowVlogList.getResult();

        for (IndexVlogVO v : result) {
            String vlogerId = v.getVlogerId();
            String vlogId = v.getVlogId();

            //判断用户是否点赞过视频
            if (StringUtils.isNotBlank(userId)) {
                //用户必定关注该博主
                boolean doIFollowVloger = fansService.queryDoIFollowVloger(userId, vlogerId);
                v.setDoIFollowVloger(doIFollowVloger);
                //判断用户是否点赞过视频
                v.setDoILikeThisVlog(doILikeVlog(userId, vlogId));
            }

            //获取当前视频被点赞过得总数
            v.setLikeCounts(getVlogBeLikedCounts(vlogId));

        }

        return setterPagedGrid(result,page);

    }

    /**
     * 查询用户点赞过的短视频
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PagedGridResult gerMyLikedVlogList(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("userId",userId);
        Page<IndexVlogVO> myLikedVlogList = vlogMapper.getMyLikedVlogList(map);
        List<IndexVlogVO> result = myLikedVlogList.getResult();
        return setterPagedGrid(result,page);
    }

    /**
     * 取消点赞/喜欢的视频
     *
     * @param userId
     * @param vlogId
     */
    @Override
    public void userUnLikeVlog(String userId, String vlogId) {
        MyLikedVlog myLikedVlog = MyLikedVlog.builder()
                .userId(userId)
                .vlogId(vlogId)
                .build();

        myLikedVlogMapper.deleteByMyLikedVlog(myLikedVlog);
    }

    /**
     * 用户喜欢/喜欢的视频
     *
     * @param userId
     * @param vlogId
     */
    @Transactional
    @Override
    public void userLikeVlog(String userId, String vlogId) {
        String rid = sid.nextShort();

        MyLikedVlog likedVlog = new MyLikedVlog();
        likedVlog.setId(rid);
        likedVlog.setVlogId(vlogId);
        likedVlog.setUserId(userId);

        myLikedVlogMapper.insert(likedVlog);


        // 系统消息：点赞短视频
        Vlog vlog = vlogMapper.selectByPrimaryKey(vlogId);
        Map msgContent = new HashMap();
        msgContent.put("vlogId", vlogId);
        msgContent.put("vlogCover", vlog.getCover());
        msgService.createMsg(userId,
                vlog.getVlogerId(),
                MessageEnum.LIKE_VLOG.type,
                msgContent);
    }


    /**
     * 改变用户视频可见状态
     *
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
    public IndexVlogVO queryVlogDetailById(String userId,String vlogId) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(vlogId)) {
            map.put("vlogId", vlogId);
        }
        List<IndexVlogVO> result = vlogMapper.queryVlogDetailById(map);
        if (result != null && result.size() > 0) {
            IndexVlogVO indexVlogVO = result.get(0);

            return setterVo(indexVlogVO,userId);
        }
        return null;
    }

    /**
     * 查询首页/搜索的vlog列表
     *
     * @param search
     * @return
     */
    @Override
    public PagedGridResult getIndexVlogList(String search, String userId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(search)) {
            map.put("search", search);
        }
        PageHelper.startPage(page, pageSize);
        Page<IndexVlogVO> listPage = vlogMapper.queryBatch(map);
        List<IndexVlogVO> result = listPage.getResult();

        for (IndexVlogVO v : result) {
            String vlogerId = v.getVlogerId();
            String vlogId = v.getVlogId();

            //判断用户是否点赞过视频
            if (StringUtils.isNotBlank(userId)) {
                // 用户是否关注该博主
                boolean doIFollowVloger = fansService.queryDoIFollowVloger(userId, vlogerId);
                v.setDoIFollowVloger(doIFollowVloger);
                v.setDoILikeThisVlog(doILikeVlog(userId, vlogId));
            }

            //获取当前视频被点赞过得总数
            v.setLikeCounts(getVlogBeLikedCounts(vlogId));

        }

        return setterPagedGrid(result, page);
    }

    @Override
    public Integer getVlogBeLikedCounts(String vlogId) {
        String countsStr = redisOperator.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId);
        if (StringUtils.isBlank(countsStr)) {
            countsStr = "0";
        }
        return Integer.valueOf(countsStr);
    }

    private boolean doILikeVlog(String myId, String vlogId) {

        String doILike = redisOperator.get(REDIS_USER_LIKE_VLOG + ":" + myId + ":" + vlogId);
        boolean isLike = false;
        if (StringUtils.isNotBlank(doILike) && doILike.equals("1")) {
            isLike = true;
        }
        return isLike;
    }

    /**
     * 新增视频
     *
     * @param vlogDTO
     */
    @Transactional
    @Override
    public void createVlog(VlogDTO vlogDTO) {
        String vid = sid.nextShort();
        Vlog vlog = new Vlog();
        BeanUtils.copyProperties(vlogDTO, vlog);
        vlog.setId(vid);
        vlog.setLikeCounts(0);
        vlog.setCommentsCounts(0);
        vlog.setIsPrivate(YesOrNo.NO.type);
//        vlog.setCreatedTime(new Date());
//        vlog.setUpdatedTime(new Date());

        vlogMapper.add(vlog);
        //vlogMapper.insert(vlog);
    }

}
