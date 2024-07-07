package com.imooc.config;

import com.imooc.properties.AliOssProperties;
import com.imooc.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api 配置类 用于创建AliOssUtil对象
 * @since 2024年 月 日
 */

@Configuration
@Slf4j
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("开始创建阿里云文件上传工具类对象：{}",aliOssProperties);
        return AliOssUtil.builder()
                .accessKeyId(aliOssProperties.getAccessKeyId())
                .accessKeySecret(aliOssProperties.getAccessKeySecret())
                .bucketName(aliOssProperties.getBucketName())
                .endpoint(aliOssProperties.getEndpoint())
                .build();
    }
}
