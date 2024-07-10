package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.vo.UsersVO;
import com.imooc.config.MinIOConfig;
import com.imooc.dto.UpdatedUserDTO;
import com.imooc.enums.FileTypeEnum;
import com.imooc.enums.UserInfoModifyType;
import com.imooc.exceptions.FileUploadErrorException;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Users;
import com.imooc.service.UserService;
import com.imooc.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@Slf4j
@Api(tags = "用户信息接口")
@RestController
@RequestMapping("userInfo")
public class UserInfoController extends BaseInfoProperties {

    @Autowired
    private UserService userService;

    @Autowired
    private MinIOConfig minIOConfig;

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("modifyImage")
    @ApiOperation(value = "修改用户头像或背景图")
    public GraceJSONResult modifyImage(MultipartFile file,
                                       @RequestParam String userId,
                                       @RequestParam Integer type) throws Exception{
        if(type != FileTypeEnum.BGIMG.type && type != FileTypeEnum.FACE.type){
            //既没有选头像 也没有选背景图
            throw new FileUploadErrorException(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

//        String originalFilename = file.getOriginalFilename();
//        //会多带一个.
//        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        //去掉开头的.
//        extension = extension.substring(1,extension.length());
//        if(StringUtils.equals(extension,"jpg")){
//            extension = "jpeg";
//        }
//        MinIOUtils.uploadFile(minIOConfig.getBucketName(),originalFilename,file.getInputStream(),"image/"+extension);
//
//        String imagUrl = minIOConfig.getFileHost() + "/" + minIOConfig.getBucketName() + "/" + originalFilename;
        UpdatedUserDTO updatedUserDTO = new UpdatedUserDTO();
        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀
            //从最后一个.开始截取
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;
            //文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), newFilename);
            updatedUserDTO.setId(userId);
            if(type == FileTypeEnum.BGIMG.type){
                updatedUserDTO.setBgImg(filePath);
            }else {
                updatedUserDTO.setFace(filePath);
            }
        } catch (IOException e) {
            log.error("文件上传失败:{}",e);
        }

        //修改图片地址到数据库
        Users users = userService.updateUserInfo(updatedUserDTO);
        return GraceJSONResult.ok(users);

    }

    @PostMapping("modifyUserInfo")
    @ApiOperation(value = "修改用户信息")
    public GraceJSONResult modifyUserInfo(@RequestBody UpdatedUserDTO updatedUserDTO,
                                          @RequestParam Integer type){
        UserInfoModifyType.checkUserInfoTypeIsRight(type);
        Users newUserInfo = userService.updateUserInfo(updatedUserDTO,type);
        return GraceJSONResult.ok(newUserInfo);
    }

    @GetMapping("query")
    @ApiOperation(value = "得到用户的信息")
    public GraceJSONResult getUserInfo(@RequestParam(value = "userId") String userId) {

        Users user = userService.getUserById(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);

        //我的关注博主总数量
        String myFollowsCountsStr = redisOperator.get(REDIS_MY_FOLLOWS_COUNTS + ":" + userId);
        //我的粉丝总数
        String myFansCountsStr = redisOperator.get(REDIS_MY_FANS_COUNTS + ":" + userId);
        //用户获赞总数 视频博主（点赞/喜欢） 综合
        //String likedVlogCountsStr = redisOperator.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + userId);
        String likedVlogerCountsStr = redisOperator.get(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + userId);

        Integer myFollowsCounts = 0;
        Integer myFansCounts = 0;
       // Integer likedVlogCounts = 0;
        Integer likedVlogerCounts = 0;
        Integer totalLikeMeCounts = 0;

        if(StringUtils.isNotBlank(myFollowsCountsStr)){
            myFollowsCounts = Integer.valueOf(myFollowsCountsStr);
        }

        if(StringUtils.isNotBlank(myFansCountsStr)){
            myFansCounts = Integer.valueOf(myFansCountsStr);
        }

//        if(StringUtils.isNotBlank(likedVlogCountsStr)){
//            likedVlogCounts = Integer.valueOf(likedVlogCountsStr);
//        }

        if(StringUtils.isNotBlank(likedVlogerCountsStr)){
            likedVlogerCounts = Integer.valueOf(likedVlogerCountsStr);
        }
        //totalLikeMeCounts = likedVlogCounts + likedVlogerCounts;
        totalLikeMeCounts =  likedVlogerCounts;
        usersVO.setMyFansCounts(myFansCounts);
        usersVO.setMyFollowsCounts(myFollowsCounts);
        usersVO.setTotalLikeMeCounts(totalLikeMeCounts);

        return GraceJSONResult.ok(usersVO);
    }
}
