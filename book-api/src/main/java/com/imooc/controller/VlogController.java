package com.imooc.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.base.BaseInfoProperties;
import com.imooc.dto.VlogDTO;
import com.imooc.enums.YesOrNo;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.mapper.VlogMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.Vlog;
import com.imooc.service.UserService;
import com.imooc.service.VlogService;
import com.imooc.utils.PagedGridResult;
import com.imooc.utils.RedisOperator;
import com.imooc.vo.IndexVlogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@RestController
@Slf4j
@Api(tags = "短视频功能接口")
@RequestMapping("vlog")
public class VlogController extends BaseInfoProperties {

    @Autowired
    private VlogService vlogService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private VlogMapper vlogMapper;

    @GetMapping("friendList")
    @ApiOperation(value = "关注列表")
    public GraceJSONResult friendList(@RequestParam String myId,
                                      @RequestParam(required = false, defaultValue = "1") Integer page,
                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        PagedGridResult pagedGridResult = vlogService.gerMyFriendVlogList(myId, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @GetMapping("followList")
    @ApiOperation(value = "关注列表")
    public GraceJSONResult followList(@RequestParam String myId,
                                      @RequestParam(required = false, defaultValue = "1") Integer page,
                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        PagedGridResult pagedGridResult = vlogService.gerMyFollowVlogList(myId, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @GetMapping("myLikedList")
    @ApiOperation("查看我喜欢的视频")
    public GraceJSONResult myLikedList(@RequestParam String userId,
                                       @RequestParam(required = false, defaultValue = "1") Integer page,
                                       @RequestParam(required = false, defaultValue = "10") Integer pageSize){

        PageHelper.startPage(page,pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("userId",userId);
        Page<IndexVlogVO> myLikedVlogList = vlogMapper.getMyLikedVlogList(map);
        List<IndexVlogVO> result = myLikedVlogList.getResult();
        return GraceJSONResult.ok(setterPagedGrid(result,page));
    }

    @PostMapping("totalLikedCounts")
    @ApiOperation(value = "计算总的数量")
    public GraceJSONResult totalLikedCounts(@RequestParam String vlogId) {
        Integer vlogBeLikedCounts = vlogService.getVlogBeLikedCounts(vlogId);
        return GraceJSONResult.ok(vlogBeLikedCounts);
    }

    @PostMapping("unlike")
    @ApiOperation(value = "取消点赞")
    public GraceJSONResult unlike(@RequestParam String userId,
                                  @RequestParam String vlogerId,
                                  @RequestParam String vlogId) {


        //我点赞的视频 从数据库中删除关联关系
        vlogService.userUnLikeVlog(userId, vlogId);

        //点赞后 视频和视频发布者的获赞都会 - 1
        redisOperator.decrement(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId, 1);
        redisOperator.decrement(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + vlogerId, 1);
        //删除redis中保存的关联关系
        redisOperator.del(REDIS_USER_LIKE_VLOG + ":" + userId + ":" + vlogId);

        return GraceJSONResult.ok();
    }

    @PostMapping("like")
    @ApiOperation(value = "")
    public GraceJSONResult like(@RequestParam String userId,
                                @RequestParam String vlogerId,
                                @RequestParam String vlogId) {


        //我点赞的视频 关联关系保存到数据库
        vlogService.userLikeVlog(userId, vlogId);

        //点赞后 视频和视频发布者的获赞都会 + 1
        redisOperator.increment(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId, 1);
        redisOperator.increment(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + vlogerId, 1);
        //我点赞的视频 需要在redis中保存关联关系
        redisOperator.set(REDIS_USER_LIKE_VLOG + ":" + userId + ":" + vlogId, "1");

        return GraceJSONResult.ok();
    }

    @GetMapping("myPrivateList")
    @ApiOperation(value = "我的私密视频")
    public GraceJSONResult myPrivateList(@RequestParam String userId,
                                         @RequestParam(required = false, defaultValue = "1") Integer page,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
//        PagedGridResult pagedGridResult = vlogAddService.queryMyVlogList(userId, page, pageSize, YesOrNo.YES.type);
        PageHelper.startPage(page, pageSize);
        Vlog vlog = new Vlog();
        vlog.setVlogerId(userId);
        vlog.setIsPrivate(YesOrNo.YES.type);
        Page<Vlog> vlogPage = vlogMapper.queryBatchByVlog(vlog);
        List<Vlog> result = vlogPage.getResult();

        PageInfo<?> pageInfo = new PageInfo<>(result);
        PagedGridResult pagedGridResult = new PagedGridResult();
        pagedGridResult.setRows(result);
        pagedGridResult.setPage(page);
        pagedGridResult.setRecords(pageInfo.getTotal());
        pagedGridResult.setTotal(pageInfo.getPages());

        return GraceJSONResult.ok(pagedGridResult);
    }

    @GetMapping("myPublicList")
    @ApiOperation(value = "我的公开视频")
    public GraceJSONResult myPublicList(@RequestParam String userId,
                                        @RequestParam(required = false, defaultValue = "1") Integer page,
                                        @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        // PagedGridResult pagedGridResult = vlogAddService.queryMyVlogList(userId, page, pageSize, YesOrNo.NO.type);

        PageHelper.startPage(page, pageSize);
        Vlog vlog = new Vlog();
        vlog.setVlogerId(userId);
        vlog.setIsPrivate(YesOrNo.NO.type);
        Page<Vlog> vlogPage = vlogMapper.queryBatchByVlog(vlog);
        List<Vlog> result = vlogPage.getResult();

        PageInfo<?> pageInfo = new PageInfo<>(result);
        PagedGridResult pagedGridResult = new PagedGridResult();
        pagedGridResult.setRows(result);
        pagedGridResult.setPage(page);
        pagedGridResult.setRecords(pageInfo.getTotal());
        pagedGridResult.setTotal(pageInfo.getPages());

        return GraceJSONResult.ok(pagedGridResult);
    }

    @PostMapping("changeToPublic")
    @ApiOperation(value = "改变视频为公开")
    public GraceJSONResult changeToPublic(@RequestParam String userId,
                                          @RequestParam String vlogId) {
        vlogService.changeToPrivateOrPublic(userId, vlogId, YesOrNo.NO.type);
        return GraceJSONResult.ok();
    }

    @PostMapping("changeToPrivate")
    @ApiOperation(value = "改变视频为私密")
    public GraceJSONResult changeToPrivate(@RequestParam String userId,
                                           @RequestParam String vlogId) {
        vlogService.changeToPrivateOrPublic(userId, vlogId, YesOrNo.YES.type);
        return GraceJSONResult.ok();
    }

    @GetMapping("detail")
    @ApiOperation(value = "不懂")
    public GraceJSONResult detail(@RequestParam(defaultValue = "", required = false) String userId,
                                  @RequestParam(required = false) String vlogId) {
        IndexVlogVO indexVlogVO = vlogService.queryVlogDetailById(userId,vlogId);
        return GraceJSONResult.ok(indexVlogVO);
    }

    @GetMapping("indexList")
    @ApiOperation(value = "查询视频")
    public GraceJSONResult indexList(@RequestParam(defaultValue = "", required = false) String search,
                                     @RequestParam(defaultValue = "", required = false) String userId,
                                     @RequestParam(required = false, defaultValue = "1") Integer page,
                                     @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        PagedGridResult indexVlogList = vlogService.getIndexVlogList(search, userId, page, pageSize);
        return GraceJSONResult.ok(indexVlogList);
    }

    @PostMapping("publish")
    @ApiOperation(value = "创建视频")
    public GraceJSONResult publish(@RequestBody VlogDTO vlogDTO) {

        vlogService.createVlog(vlogDTO);
        return GraceJSONResult.ok();
    }

}
