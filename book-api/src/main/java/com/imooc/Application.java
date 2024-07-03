package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 小小低头哥
 * @version v1.0
 * @api
 * @since 2024年 月 日
 */

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@Transactional
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
