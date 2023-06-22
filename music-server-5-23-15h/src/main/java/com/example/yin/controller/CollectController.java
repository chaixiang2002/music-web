package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.model.dto.CollectDto;
import com.example.yin.service.CollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@Api(tags = "用户收藏歌曲接口", value = "用户收藏歌曲接口")
@Validated
public class CollectController {

    @Autowired
    private CollectService collectService;


    // 添加收藏的歌曲
    //前台界面逻辑
    @PostMapping("/collection/add")
    @ApiOperation(tags = "添加收藏歌曲",
            value = "用户点击收藏歌曲的话，就往用户收藏歌曲表添加一条记录"
    )
    public R addCollection(@RequestBody @Validated CollectDto addCollectRequest) {
        return collectService.addCollection(addCollectRequest);
    }


    // 取消收藏的歌曲
    @DeleteMapping("/collection/delete")
    @ApiOperation(tags = "取消收藏歌曲", value = "往用户收藏表删除一条记录")
    public R deleteCollection( @RequestParam("userId") @NotNull(message = "用户id不能为空") Integer userId,
                               @RequestParam("songId") @NotNull(message = "歌曲id不能为空") Integer songId) {
        return collectService.deleteCollect(userId, songId);
    }

    // 是否已收藏歌曲
    @PostMapping("/collection/status")
    @ApiOperation(tags = "查询用户是否已收藏歌曲", value = "根据用户id和歌曲id在用户歌曲收藏表中查找记录")
    public R isCollection(@RequestBody @Validated CollectDto isCollectRequest) {
        return collectService.existSongId(isCollectRequest);
    }

    // 返回的指定用户 ID 收藏的列表
    @GetMapping("/collection/detail")
    @ApiOperation(tags = "查询用户收藏的全部歌曲", value = "根据用户id，查询用户在歌曲收藏表中的全部数据")
    public R collectionOfUser(@RequestParam @NotNull(message = "用户id不能为空")
                              Integer userId) {
        return collectService.collectionOfUser(userId);
    }
}
