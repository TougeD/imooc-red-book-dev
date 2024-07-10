package com.imooc.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.FansMapper;
import com.imooc.pojo.Fans;
import com.imooc.service.FansService;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.FansVO;
import com.imooc.vo.VlogerVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

@Component
public class FansServiceImpl extends BaseInfoProperties implements FansService {

    @Autowired
    private FansMapper fansMapper;

    @Autowired
    private Sid sid;

    /**
     * 查询我的粉丝列表
     * @param myId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PagedGridResult queryMyFans(String myId, Integer page, Integer pageSize) {

        /**
         * <判断粉丝是否是我的朋友（互粉互关）>
         *     普通做法：
         *     多表关联+嵌套关联查询  这样会违反多表关联的规范 不可取 高并发下会出现性能问题
         *     常规做法：
         *     1. 避免过多的表关联查询，先查询我的粉丝列表 获得fansList
         *     2. 判断粉丝关注我 并且我也关注粉丝 -> 循环fansList 获得每一个粉丝 再去数据库查询我是否关注他
         *     3. 如果我也关注他（粉丝） 说明 我两互为朋友关系（互关互粉） 则标记flag为true 否则为false
         *
         *     高端做法：
         *     1. 关注/取关的时候  关联关系保存在redis中 不要以来数据库
         *     2. 数据库查询后，直接循环查询redis 避免第二次循环查询数据库的尴尬
         */

        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);
        PageHelper.startPage(page,pageSize);
        Page<FansVO> fansVOS = fansMapper.queryMyFans(map);

        List<FansVO> fansVOList = fansVOS.getResult();
        for(FansVO fansVO : fansVOList){
            String relationship = redisOperator.get(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + fansVO.getFanId());
            if(StringUtils.isNotBlank(relationship) && relationship.equals("1")){
                fansVO.setFriend(true);
            }
        }

        return setterPagedGrid(fansVOList,page);
    }

    /**
     * @param myId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PagedGridResult queryMyFollows(String myId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);
        PageHelper.startPage(page,pageSize);
        Page<VlogerVO> vlogerVOS = fansMapper.queryMyFollows(map);

        return setterPagedGrid(vlogerVOS.getResult(),page);
    }

    /**
     * 查询用户是否关注博主
     *
     * @param myId
     * @param vlogerId
     * @return
     */
    @Override
    public boolean queryDoIFollowVloger(String myId, String vlogerId) {
        Fans vloger = queryFansRelationShip(myId, vlogerId);

        return vloger != null;
    }

    /**
     * 取消关注
     *
     * @param myId
     * @param vlogerId
     */
    @Override
    public void doCancel(String myId, String vlogerId) {
        // 判断是否是朋友关系 如果是 需要取消双方关系
        Fans fans = queryFansRelationShip(myId, vlogerId);
        if (fans != null && fans.getIsFanFriendOfMine() == YesOrNo.YES.type) {
            //抹除双方的朋友关系 自己的关系删除即可
            Fans pendingFan = queryFansRelationShip(vlogerId, myId);
            pendingFan.setIsFanFriendOfMine(YesOrNo.NO.type);
            fansMapper.updateById(pendingFan);
        }
        //删除自己的关注关联表记录
        fansMapper.deleteById(fans.getId());
    }

    /**
     * 关注
     *
     * @param myId
     * @param vlogerId
     */
    @Transactional
    @Override
    public void doFollow(String myId, String vlogerId) {

        String fid = sid.nextShort();
        Fans fans = new Fans();
        fans.setId(fid);
        fans.setFanId(myId);
        fans.setVlogerId(vlogerId);
        //判断对方是否关注我
        Fans vloger = queryFansRelationShip(vlogerId, myId);
        if (vloger != null) {
            //说明互为朋友
            fans.setIsFanFriendOfMine(YesOrNo.YES.type);
            vloger.setIsFanFriendOfMine(YesOrNo.YES.type);
            //更新被关注者的朋友信息
            fansMapper.updateById(vloger);
        } else {
            //说明是单向奔赴 呜呜呜呜呜
            fans.setIsFanFriendOfMine(YesOrNo.NO.type);
        }
        //将新的关系添加进表中
        fansMapper.add(fans);
    }

    public Fans queryFansRelationShip(String fanId, String vlogerId) {
        Fans fans = new Fans();
        fans.setVlogerId(vlogerId);
        fans.setFanId(fanId);
        //关注人和被关注人是一对一关系
        Fans findFans = fansMapper.queryFansByFans(fans);
        return findFans;
    }
}
