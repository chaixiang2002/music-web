package com.example.yin.model.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @Author 祝英台炸油条
 * @Time : 2022/6/6 16:57
 **/
@Data
public class RankListDto {
    private Long id;

    @NotNull(message = "歌曲列表id不能为空")
    private Long songListId;

    @NotNull(message = "用户id不能为空")
    private Long consumerId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1)
    @Max(value = 10)
    private Integer score;
}
