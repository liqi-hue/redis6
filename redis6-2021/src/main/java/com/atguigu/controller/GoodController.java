package com.atguigu.controller;

import com.atguigu.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class GoodController {

    public static final String KEY = "atguigu:key";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/buy_goods")
    public synchronized String buy_Goods() throws Exception {
        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
        try {
            //setnx+expire不安全，两条命令非原子性的
            //***使用 set key value [EX seconds] [PX milliseconds] [NX|XX]
            //***加锁并设置过期时间(原子性设置过期时间：防止加锁之后微服务宕机没有设置过期时间)
            Boolean ifAbsent = stringRedisTemplate.opsForValue().setIfAbsent(KEY, value, 20L, TimeUnit.SECONDS);
            if (!ifAbsent) {
                return "加锁失败";
            }
            String result = stringRedisTemplate.opsForValue().get("goods:001");
            int goodsNumber = result == null ? 0 : Integer.parseInt(result);
            if (goodsNumber > 0) {
                int realNumber = goodsNumber - 1;
                stringRedisTemplate.opsForValue().set("goods:001", realNumber + "");
                System.out.println("你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort);
                return "你已经成功秒杀商品，此时还剩余：" + realNumber + "件" + "\t 服务器端口：" + serverPort;
            } else {
                System.out.println("商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort);
            }
            return "商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //***删除锁一定要在finally中防止上面程序运行时异常不能释放锁
            //***查询value和删除要是原子性的(防止误删，自己的key已过期别人拿到锁后把别人的锁删除了)
//            String s = stringRedisTemplate.opsForValue().get(KEY);
//            if (value.equals(s)) {
//                stringRedisTemplate.delete(KEY);
//            }
            Jedis jedis = RedisUtils.getJedis();
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] " + "then " + "return redis.call('del', KEYS[1]) " + "else " + "   return 0 " + "end";
            try {
                Object result = jedis.eval(script, Collections.singletonList(KEY), Collections.singletonList(value));
                if ("1".equals(result.toString())) {
                    System.out.println("------del REDIS_LOCK_KEY success");
                } else {
                    System.out.println("------del REDIS_LOCK_KEY error");
                }
            } finally {
                if (null != jedis) {
                    jedis.close();
                }
            }
        }
        return "商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口：" + serverPort;
    }

}