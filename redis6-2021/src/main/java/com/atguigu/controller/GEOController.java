package com.atguigu.controller;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: liqi
 * @create 2022-12-18 1:19 PM
 */
@RestController
public class GEOController {

    private static final String CITY = "city";

    @Resource
    private RedisTemplate redisTemplate;

    @PostMapping("/geoadd")
    public String geoAdd(){
        Map<String, Point> map = new HashMap<>();
        map.put("天安门",new Point(116.403963,39.915119));
        map.put("故宫",new Point(116.403414,39.924091));
        map.put("长城",new Point(116.024067,40.362639));
        redisTemplate.opsForGeo().add(CITY,map);
        return map.toString();
    }

    @GetMapping("/geopos/{member}")
    public Point position(@PathVariable String member){
        List position = redisTemplate.opsForGeo().position(CITY, member);
        return (Point) position.get(0);
    }

    @GetMapping("/geohash/{member}")
    public Point hash(@PathVariable String member){
        Object o = redisTemplate.opsForGeo().hash(CITY, member).get(0);
        return (Point) o;
    }

    @GetMapping("/geodist/{member1}/{member2}")
    public Distance dist(@PathVariable String member1,
                      @PathVariable String member2){
        Distance distance = redisTemplate.opsForGeo().distance(CITY, member1, member2, Metrics.KILOMETERS);
        return distance;
    }
}
