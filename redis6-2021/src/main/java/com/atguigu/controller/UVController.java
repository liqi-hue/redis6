package com.atguigu.controller;

import com.atguigu.service.UVService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: liqi
 * @create 2022-12-18 10:53 AM
 */
@RestController
public class UVController {

    @Resource
    private UVService uvService;


    @GetMapping("/uv")
    public Long uv(){
        return uvService.uv();
    }

}
