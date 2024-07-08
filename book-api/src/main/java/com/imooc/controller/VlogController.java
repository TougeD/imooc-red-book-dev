package com.imooc.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.dto.VlogDTO;
import com.imooc.enums.YesOrNo;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.mapper.VlogMapper;
import com.imooc.pojo.Vlog;
import com.imooc.service.VlogAddService;
import com.imooc.service.VlogService;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.IndexVlogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
public class VlogController {

    @Autowired
    private VlogService vlogService;

    @Autowired
    private VlogAddService vlogAddService;

    @Autowired
    private VlogMapper vlogMapper;

    @GetMapping("myPrivateList")
    @ApiOperation(value = "我的私密视频")
    public GraceJSONResult myPrivateList(@RequestParam String userId,
                                        @RequestParam(required = false,defaultValue = "1") Integer page,
                                        @RequestParam(required = false,defaultValue = "10") Integer pageSize){
//        PagedGridResult pagedGridResult = vlogAddService.queryMyVlogList(userId, page, pageSize, YesOrNo.YES.type);
        PageHelper.startPage(page,pageSize);
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
                                        @RequestParam(required = false,defaultValue = "1") Integer page,
                                        @RequestParam(required = false,defaultValue = "10") Integer pageSize){
       // PagedGridResult pagedGridResult = vlogAddService.queryMyVlogList(userId, page, pageSize, YesOrNo.NO.type);

        PageHelper.startPage(page,pageSize);
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
                                           @RequestParam String vlogId){
        vlogService.changeToPrivateOrPublic(userId,vlogId, YesOrNo.NO.type);
        return GraceJSONResult.ok();
    }

    @PostMapping("changeToPrivate")
    @ApiOperation(value = "改变视频为私密")
    public GraceJSONResult changeToPrivate(@RequestParam String userId,
                                           @RequestParam String vlogId){
        vlogService.changeToPrivateOrPublic(userId,vlogId, YesOrNo.YES.type);
        return GraceJSONResult.ok();
    }

    @GetMapping("detail")
    @ApiOperation(value = "不懂")
    public GraceJSONResult detail(@RequestParam(defaultValue = "",required = false) String userId,
                                  @RequestParam(required = false) String vlogId){
        IndexVlogVO indexVlogVO = vlogService.queryVlogDetailById(vlogId);
        return GraceJSONResult.ok(indexVlogVO);
    }

    @GetMapping("indexList")
    @ApiOperation(value = "查询视频")
    public GraceJSONResult indexList(@RequestParam(defaultValue = "",required = false) String search,
                                     @RequestParam(required = false,defaultValue = "1") Integer page,
                                     @RequestParam(required = false,defaultValue = "10") Integer pageSize){

        PagedGridResult indexVlogList = vlogService.getIndexVlogList(search, page, pageSize);
        return GraceJSONResult.ok(indexVlogList);
    }

    @PostMapping("publish")
    @ApiOperation(value = "创建视频")
    public GraceJSONResult publish(@RequestBody VlogDTO vlogDTO){

        vlogService.createVlog(vlogDTO);
        return GraceJSONResult.ok();
    }

}
