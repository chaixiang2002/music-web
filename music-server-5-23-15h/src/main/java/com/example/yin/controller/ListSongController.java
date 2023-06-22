package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.model.dto.ListSongDto;
import com.example.yin.model.dto.UpdateSongListDto;
import com.example.yin.service.ListSongService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(tags = "歌单接口", value = "歌单接口")
@Validated
public class ListSongController {

    @Autowired
    private ListSongService listSongService;


    @PostMapping("/listSong/preUpdate")
    @ApiOperation(tags = "获取歌单中没有的歌曲", value = "获取歌单中没有的歌曲")
    public R getSongNotInSongList(@Param("songListId") @NotNull(message = "歌单id不能为空") Integer songListId) {
        return listSongService.getSongNotInSongList(songListId);
    }

    @PostMapping("/listSong/update")
    @ApiOperation(tags = "更新歌单中的歌曲", value = "更新歌单中的歌曲")
    public R changeSongInSongList(@RequestBody @Validated UpdateSongListDto updateSongListDto) {
        String newSongIdList = updateSongListDto.getSongIds();
        List<Integer> collect = new ArrayList<>();
        if (!ObjectUtils.isEmpty(newSongIdList)) {
            try {
                collect = Arrays.stream(newSongIdList.split(",")).map(o -> Integer.valueOf(o)).collect(Collectors.toList());
            } catch (Exception e) {
                return R.error("更新失败");
            }
        }
        return listSongService.updateSongInSongList(collect, updateSongListDto.getSongListId());
    }


    // 给歌单添加歌曲
    @PostMapping("/listSong/add")
    @ApiOperation(tags = "往歌单添加歌曲", value = "往歌单添加歌曲")
    public R addListSong(@RequestBody @Validated ListSongDto addListSongRequest) {
        return listSongService.addListSong(addListSongRequest);
    }

    // 删除歌单里的歌曲
    @GetMapping("/listSong/delete")
    @ApiOperation(tags = "删除歌单中的歌曲", value = "删除歌单中的歌曲")
    public R deleteListSong(@RequestParam @NotNull(message = "歌单id不能为空") int songListId,
                            @RequestParam @NotNull(message = "歌曲id不能为空") int songId) {
        return listSongService.deleteListSong(songId, songListId);
    }

    // 返回歌单里的歌曲列表
    @GetMapping("/listSong/detail")
    @ApiOperation(tags = "获取歌单中歌曲列表", value = "获取歌单中歌曲列表")
    public R listSongOfSongId(@RequestParam @NotNull(message = "歌单id不能为空") int songListId) {
        return listSongService.listSongOfSongId(songListId);
    }

}
