package com.example.yin.controller;

import com.example.yin.annotation.ByteEnumRule;
import com.example.yin.common.R;
import com.example.yin.model.dto.SingerDto;
import com.example.yin.service.SingerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@RestController
@Api(tags = "歌手接口", value = "歌手接口")
@Validated
public class SingerController {

    @Autowired
    private SingerService singerService;


    // 添加歌手
    @PostMapping("/singer/add")
    @ApiOperation(tags = "添加歌手", value = "添加歌手")
    public R addSinger(@RequestBody @Validated SingerDto addSingerRequest) {
        return singerService.addSinger(addSingerRequest);
    }

    // 删除歌手
    @DeleteMapping("/singer/delete")
    @ApiOperation(tags = "删除歌手", value = "删除歌手")
    public R deleteSinger(@RequestParam @NotNull int id) {
        return singerService.deleteSinger(id);
    }

    // 返回所有歌手
    @GetMapping("/singer")
    @ApiOperation(tags = "查询全部歌手", value = "查询全部歌手")
    public R allSinger() {
        return singerService.allSinger();
    }

    // 根据歌手名查找歌手
    @GetMapping("/singer/name/detail")
    @ApiOperation(tags = "根据歌手名称查找歌手", value = "根据歌手名称查找歌手")
    public R singerOfName(@RequestParam @NotNull(message = "歌手名称不能为空") String name) {
        return singerService.singerOfName(name);
    }

    // 根据歌手性别查找歌手
    @GetMapping("/singer/sex/detail")
    @ApiOperation(tags = "根据歌手性别", value = "根据歌手性别")
    public R singerOfSex(@RequestParam @ByteEnumRule(rule = "0,1,2,3", message = "歌手性别只能是0-女 1-男 2-报名 3-不明,不能为空", notNull = true)
                         Byte sex) {
        return singerService.singerOfSex(sex);
    }

    // 更新歌手信息
    @PostMapping("/singer/update")
    public R updateSingerMsg(@RequestBody @Validated SingerDto updateSingerRequest) {
        return singerService.updateSingerMsg(updateSingerRequest);
    }

    // 更新歌手头像
    @PostMapping("/singer/avatar/update")
    public R updateSingerPic(@RequestParam("file")  MultipartFile avatorFile,
                             @RequestParam("id") @NotNull(message = "用户id不能为空") int id) {
        return singerService.updateSingerPic(avatorFile, id);
    }
}
