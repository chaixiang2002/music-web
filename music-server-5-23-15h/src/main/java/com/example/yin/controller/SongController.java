package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.model.dto.AddSongDto;
import com.example.yin.model.dto.SongScoreDto;
import com.example.yin.model.dto.UpDateSongInfoDto;
import com.example.yin.service.RecommendService;
import com.example.yin.service.SongService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.MultipartConfigElement;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Api(tags = "歌曲接口", value = "歌曲接口")
@Validated
public class SongController {

    @Autowired
    private SongService songService;

    @Autowired
    private RecommendService recommendService;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 文件最大20M,DataUnit提供5中类型B,KB,MB,GB,TB
        factory.setMaxFileSize(DataSize.of(20, DataUnit.MEGABYTES));

        // 设置总上传数据总大小20M
        factory.setMaxRequestSize(DataSize.of(20, DataUnit.MEGABYTES));
        return factory.createMultipartConfig();
    }

    @GetMapping("/song/recommentSong")
    public R recommentSongs() {
        List<SongScoreDto> scoreDtos = recommendService.recommendSongList();
        return R.success("推荐歌曲如下", scoreDtos);
    }


    //播放歌曲
    @PostMapping("/song/playbackCount")
    public R addSongPlayBackCount(@NotNull(message = "用户id不能为空") Integer consumerId,
                                  @NotNull(message = "歌曲id不能为空") Integer songId) {
        return songService.addSongPlayBackCount(consumerId, songId);
    }


    // 添加歌曲
    @PostMapping("/song/add")
    public R addSong(@Validated AddSongDto addSongRequest,
                     @RequestParam("file") MultipartFile file) {

        return songService.addSong(addSongRequest, file);
    }

    // 删除歌曲
    @DeleteMapping("/song/delete")
    public R deleteSong(@RequestParam int id) {
        return songService.deleteSong(id);
    }

    // 返回所有歌曲
    @GetMapping("/song")
    public R allSong() {
        return songService.allSong();
    }

    // 返回指定歌曲ID的歌曲
    @GetMapping("/song/detail")
    public R songOfId(@RequestParam @NotNull int id) {
        return songService.songOfId(id);
    }

    // 返回指定歌手ID的歌曲
    @GetMapping("/song/singer/detail")
    public R songOfSingerId(@RequestParam @NotNull int singerId) {
        return songService.songOfSingerId(singerId);
    }

    // 返回指定歌手名的歌曲
    @GetMapping("/song/singerName/detail")
    public R songOfSingerName(@RequestParam @NotNull String name) {
        return songService.songOfSingerName(name);
    }

    // 更新歌曲信息
    @PostMapping("/song/update")
    public R updateSongMsg(@RequestBody @Validated UpDateSongInfoDto updateSongRequest) {
        return songService.updateSongMsg(updateSongRequest);
    }

    // 更新歌曲图片
    @PostMapping("/song/img/update")
    public R updateSongPic(@RequestParam("file") MultipartFile file, @RequestParam("id") @NotNull int id) {
        return songService.updateSongPic(file, id);
    }

    // 更新歌曲
    @PostMapping("/song/url/update")
    public R updateSongUrl(@RequestParam("file") @NotNull MultipartFile urlFile, @RequestParam("id") @NotNull int id) {
        return songService.updateSongUrl(urlFile, id);
    }


}
