package com.imooc.controller;

import com.imooc.grace.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@RestController
@Api(tags = "Hello 测试的接口")
public class HelloController {

    @GetMapping("hello")
    @ApiOperation("这是一个测试hello的测试路由")
    public Object hello(){
        return GraceJSONResult.ok();
    }

}
