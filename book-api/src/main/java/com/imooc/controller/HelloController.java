package com.imooc.controller;

import com.imooc.base.RabbitMQConfig;
import com.imooc.grace.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
@Slf4j
@RefreshScope
public class HelloController {

    @Value("${nacos.counts}")
    private Integer nacosCounts;

    @GetMapping("nacosCounts")
    @ApiOperation("配置文件的参数")
    public Object nacosCounts() {
        log.info("nacosCounts：{}", nacosCounts);
        return GraceJSONResult.ok("nacosCounts：" + nacosCounts);
    }

    @GetMapping("hello")
    @ApiOperation("这是一个测试hello的测试路由")
    public Object hello() {
        return GraceJSONResult.ok();
    }

    @Autowired
    public RabbitTemplate rabbitTemplate;

    @GetMapping("produce")
    @ApiOperation("生产者发消息")
    public Object produce() {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MSG, "sys.msg.send", "我发了一个消息");

        /**
         * 路由规则
         * route-key
         * display.*.*
         *      displau.a.b
         *      displau.public.msg
         *      display.a.b.c   这个匹配不到  因为*代表一个占位符
         * display.#
         *      displau.a.b
         *      displau.public.msg
         *      display.a.b.c
         *      display.a.b.c.d.e
         *      #代表多个占位符
         */

        return GraceJSONResult.ok();
    }

    @GetMapping("produce2")
    @ApiOperation("生产者发消息")
    public Object produce2() {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MSG, "sys.msg.delete", "我删除了一个消息");

        return GraceJSONResult.ok();
    }
}
