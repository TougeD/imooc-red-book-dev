package com.imooc.controller;

import com.imooc.config.MinIOConfig;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.utils.MinIOUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@RestController
@Api(tags = "文件上传测试的接口")
@Slf4j
public class FileController {

    @Autowired
    private MinIOConfig minIOConfig;

//    @PostMapping("upload")
//    public GraceJSONResult upload(MultipartFile file) throws Exception{
//
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
//        return GraceJSONResult.ok(imagUrl);
//    }
}
