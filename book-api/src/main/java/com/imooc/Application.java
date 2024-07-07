package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.service.Contact;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@EnableTransactionManagement
@MapperScan(basePackages = "com.imooc.mapper")
@ComponentScan(basePackages = {"com.imooc","org.n3r.idworker"}) //扫描组件包
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
