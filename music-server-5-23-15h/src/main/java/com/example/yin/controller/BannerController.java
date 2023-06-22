package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.service.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 祝英台炸油条
 * @Time : 2022/6/13 13:16
 **/
@RestController
@RequestMapping("/banner")
@Api(tags = "轮播图接口")
@Validated
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping("/getAllBanner")
    @ApiOperation(tags = "获取轮播图列表url",value = "获取轮播图列表url")
    public R getAllBanner(){
        return R.success("成功获取轮播图与",bannerService.getAllBanner());
    }
}
