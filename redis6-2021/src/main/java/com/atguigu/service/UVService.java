package com.atguigu.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author: liqi
 * @create 2022-12-18 10:53 AM
 */
@Service
public class UVService implements InitializingBean {

    @Resource
    private RedisTemplate redisTemplate;

    // 低流量用set也行
    public Long uv(){
        return redisTemplate.opsForHyperLogLog().size("hll");
    }

    //初始化一些ip
    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            String ip = "";
            Random random = new Random();
            for (int i = 1; i <= 200; i++) {
                ip = random.nextInt(255) + "." +
                        random.nextInt(255) + "." +
                        random.nextInt(255) + "." +
                        random.nextInt(255);
                redisTemplate.opsForHyperLogLog().add("hll",ip);
                try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        },"t1").start();
    }
}
