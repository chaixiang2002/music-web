package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.model.dto.RankListDto;
import com.example.yin.service.RankListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@Validated
@Api(tags = "评分接口", value = "评分接口")
public class RankListController {

    @Autowired
    private RankListService rankListService;


    // 提交评分
    @PostMapping("/rankList/add")
    @ApiOperation(tags = "提交评分", value = "提交评分")
    public R addRank(@RequestBody @Validated RankListDto rankListAddRequest) {
        return rankListService.addorUpdateRank(rankListAddRequest);
    }

    // 获取指定歌单的评分
    @GetMapping("/rankList")
    @ApiOperation(tags = "获取指定歌单的评分", value = "获取指定歌单的评分")
    public R rankOfSongListId(@RequestParam @NotNull(message = "歌单id不能为空") Long songListId) {
        return rankListService.rankOfSongListId(songListId);
    }

    // 获取指定用户的歌单评分
    @GetMapping("/rankList/user")
    @ApiOperation(tags = "获取指定用户的歌单评分", value = "获取指定用户的歌单评分")
    public R getUserRank(@RequestParam("consumerId")  Long consumerId,
                         @RequestParam("songListId") @NotNull(message = "歌单id不能为空") Long songListId) {
        return rankListService.getUserRank(consumerId, songListId);
    }
}
