package com.example.yin.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author 祝英台炸油条
 * @Time : 2022/6/6 20:38
 **/
@Data
public class ListSongDto {

    private Integer id;

    @NotNull(message = "歌曲id不能为空")
    private Integer songId;

    @NotNull(message = "歌单id不能为空")
    private Integer songListId;
}
