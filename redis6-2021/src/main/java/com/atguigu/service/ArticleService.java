package com.atguigu.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: liqi
 * @create 2022-12-17 1:40 PM
 */
@Slf4j
@Service
public class ArticleService {

    public static final String ARTICLE = "article:";
    @Resource
    private RedisTemplate redisTemplate;


    //中小厂可用
    public void likeArticle(String articleId){
        String key = ARTICLE + articleId;
        Long likeNumbers = redisTemplate.opsForValue().increment(key);
        log.info("文章阅读量：{}",likeNumbers);
    }

}
