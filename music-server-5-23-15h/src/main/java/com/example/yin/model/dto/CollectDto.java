package com.example.yin.model.dto;

import com.example.yin.annotation.ByteEnumRule;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author 祝英台炸油条
 * @Time : 2022/6/6 13:11
 **/
@Data
public class CollectDto {

    @NotNull(message = "用户id不能为空")
    private Integer userId;

    @ByteEnumRule(rule = "0,1",message = "收藏类型只有0，1两种,不能为空",notNull = true)
    private Byte type;

    @NotNull(message = "歌曲id不能为空")
    private Integer songId;

    private Integer songListId;

}
