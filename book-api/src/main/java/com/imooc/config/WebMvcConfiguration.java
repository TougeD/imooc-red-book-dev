package com.imooc.config;

import com.imooc.interceptor.PassportInterceptor;
import com.imooc.interceptor.UserTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private PassportInterceptor passportInterceptor;

    @Autowired
    private UserTokenInterceptor userTokenInterceptor;

    /**
     * 自定义拦截器
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {

        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(passportInterceptor)
                .addPathPatterns("/passport/getSMSCode");

        registry.addInterceptor(userTokenInterceptor)
                .addPathPatterns("/userInfo/modifyUserInfo")
                .addPathPatterns("/userInfo/modifyImage");
    }

    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始设置静态资源映射...");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

//    /**
//     * 通过knife4j生成接口文档
//     *
//     * @return
//     */
//    @Bean
//    public Docket docket1() {
//        log.info("准备生成接口文档");
//        ApiInfo apiInfo = new ApiInfoBuilder()
//                .title("小小低头哥短视频实战接口文档")
//                .version("2.0")
//                .description("小小低头哥短视频实战接口文档")
//                .build();
//        Docket docket = new Docket(DocumentationType.SWAGGER_2)
//                .groupName("接口")
//                .apiInfo(apiInfo)
//                .select()
//                //指定生成需要扫描的包
//                .apis(RequestHandlerSelectors.basePackage("com.imooc.controller"))
//                .paths(PathSelectors.any())
//                .build();
//        return docket;
//    }
}
