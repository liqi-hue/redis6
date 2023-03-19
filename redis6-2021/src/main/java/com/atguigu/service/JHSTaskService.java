package com.atguigu.service;

import cn.hutool.core.date.DateUtil;
import com.atguigu.consts.Constants;
import com.atguigu.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JHSTaskService {
    @Autowired
    private RedisTemplate redisTemplate;

    /** 缓存击穿是什么：
     * 大量的请求同时查询一个 key 时，
     * 此时这个key正好失效了，就会导致大量的请求都打到数据库上面去
     * 简单说就是热点key突然失效了，暴打mysql
     * 危害:会造成某一时刻数据库请求量过大，压力剧增。
     * 解决方案：
     * 方案2：对于访问频繁的热点key，干脆就不设置过期时间
     * 方案3：互斥独占锁防止击穿
     */

//    @PostConstruct
    public void initJHS() {
        log.info("启动定时器淘宝聚划算功能模拟.........." + DateUtil.now());
        new Thread(() -> {
            //模拟定时器，定时把数据库的特价商品，刷新到redis中
            while (true) {
                //模拟从数据库读取100件特价商品，用于加载到聚划算的页面中
                List list = this.products();
                //采用redis list数据结构的lpush来实现存储,这里存在缓存击穿()
                this.redisTemplate.delete(Constants.JHS_KEY);
                //lpush命令
                this.redisTemplate.opsForList().leftPushAll(Constants.JHS_KEY, list);
                //间隔一分钟 执行一遍
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("runJhs定时刷新..............");
            }
        }, "t1").start();
    }

    /**
     * 模拟从数据库读取100件特价商品，用于加载到聚划算的页面中
     */
    public List products() {
        List list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Random rand = new Random();
            int id = rand.nextInt(10000);
            Product obj = new Product((long) id, "product" + i, i, "detail");
            list.add(obj);
        }
        return list;
    }


    //双缓存结构来解决缓存击穿
    @PostConstruct
    public void initJHSAB() {
        log.info("启动AB定时器计划任务淘宝聚划算功能模拟.........." + DateUtil.now());
        new Thread(() -> {
//            模拟定时器，定时把数据库的特价商品，刷新到redis中
            while (true) {
                //模拟从数据库读取100件特价商品，用于加载到聚划算的页面中
                List list = this.products();
                //先更新B缓存
                this.redisTemplate.delete(Constants.JHS_KEY_B);
                this.redisTemplate.opsForList().leftPushAll(Constants.JHS_KEY_B, list);
                this.redisTemplate.expire(Constants.JHS_KEY_B, 20L, TimeUnit.DAYS);
                //再更新A缓存
                this.redisTemplate.delete(Constants.JHS_KEY_A);
                this.redisTemplate.opsForList().leftPushAll(Constants.JHS_KEY_A, list);
                this.redisTemplate.expire(Constants.JHS_KEY_A, 15L, TimeUnit.DAYS);
                //间隔一分钟 执行一遍
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("runJhs定时刷新..............");
            }
        }, "t1").start();
    }

}