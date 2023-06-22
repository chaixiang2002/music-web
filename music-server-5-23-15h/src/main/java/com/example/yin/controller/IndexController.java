package com.example.yin.controller;

import com.example.yin.service.IndexServie;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@Api(tags = "基础接口", value = "基础接口")
@Validated
public class IndexController {

    @Resource
    private IndexServie indexServie;

    @GetMapping("/download/song/{id}")
    @ApiOperation(tags = "下载音乐资源",value = "下载音乐资源")
    public void downloadSong(@PathVariable("id") Integer songId) {
        indexServie.downloadSong(songId);
    }

}
