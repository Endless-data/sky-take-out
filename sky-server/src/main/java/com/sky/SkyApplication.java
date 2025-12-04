package com.sky;

import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
@EnableCaching // 开启缓存支持
@EnableScheduling // 开启定时任务支持
public class SkyApplication {

    @Autowired
    OrderMapper orderMapper;

    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        log.info("server started");

        // select * from orders where status =
    }
}
