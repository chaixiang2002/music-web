package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.mapper.ListSongMapper;
import com.example.yin.model.dto.AddSongListDto;
import com.example.yin.model.dto.SongListDto;
import com.example.yin.service.SongListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@RestController
@Api(tags = "歌单接口",value = "歌单接口")
@Validated
public class SongListController {

    @Autowired
    private SongListService songListService;


    // 添加歌单
    @PostMapping("/songList/add")
    @ApiOperation(tags = "添加歌单",value = "添加歌单")
    public R addSongList(@RequestBody @Validated AddSongListDto addSongListRequest) {
        return songListService.addSongList(addSongListRequest);
    }

    // 删除歌单
    @GetMapping("/songList/delete")
    @ApiOperation(tags = "删除歌单",value = "删除歌单")
    public R deleteSongList(@RequestParam @NotNull(message = "歌单id不能为空") int id) {
        return songListService.deleteSongList(id);
    }


    // 返回所有歌单
    @GetMapping("/songList")
    @ApiOperation(tags = "获取歌单列表",value = "获取歌单列表")
    public R allSongList() {
        return songListService.allSongList();
    }

    // 返回标题包含文字的歌单
    @GetMapping("/songList/likeTitle/detail")
    @ApiOperation(tags = "根据文字搜索歌单",value = "根据文字搜索歌单")
    public R songListOfLikeTitle(@RequestParam @NotNull(message = "搜索文字不能为空") String title) {
        return songListService.likeTitle( title );
    }

    // 返回指定类型的歌单
    @GetMapping("/songList/style/detail")
    @ApiOperation(tags = "根据风格搜索歌单",value = "根据风格搜索歌单")
    public R songListOfStyle(@RequestParam @NotNull(message = "歌单风格不能为空") String style) {
        return songListService.likeStyle(style);
    }

    // 更新歌单信息
    @PostMapping("/songList/update")
    @ApiOperation(tags = "更新歌单信息",value = "更新歌单信息")
    public R updateSongListMsg(@RequestBody @Validated SongListDto updateSongListRequest) {
        return songListService.updateSongListMsg(updateSongListRequest);

    }

    // 更新歌单图片
    @PostMapping("/songList/img/update")
    @ApiOperation(tags = "更新歌单图片",value = "更新歌单图片")
    public R updateSongListPic(@RequestParam("file") @NotNull(message = "图片资源文件不能为空") MultipartFile avatorFile,
                               @RequestParam("id") @NotNull(message = "歌单id不能为空") int id) {
        return songListService.updateSongListImg(avatorFile,id);
    }
}
