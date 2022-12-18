package com.atguigu.controller;

import com.atguigu.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: liqi
 * @create 2022-12-17 1:39 PM
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;


    //微信文章阅读量统计
    @GetMapping("/view/{articleId}")
    public void likeArticle(@PathVariable("articleId") String articleId){
        articleService.likeArticle(articleId);
    }

}
