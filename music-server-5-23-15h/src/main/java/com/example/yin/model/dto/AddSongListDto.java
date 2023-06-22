package com.example.yin.model.dto;

import com.example.yin.annotation.StringEnumRule;
import com.example.yin.annotation.StringLengthRule;
import lombok.Data;

@Data
public class AddSongListDto {


    @StringLengthRule(min = 1, max = 50, message = "歌单名长度在1~50,不能为空", notNull = true)
    private String title;

    @StringEnumRule(rules = "华语,粤语,欧美,日韩,轻音乐,BGM,乐器", message = "歌单类型只能在规定范围内进行选择哦,不能为空", notNull = true)
    private String style;

    @StringLengthRule(min = 10, max = 250, message = "歌单介绍数据范围是10到250字,不能为空", notNull = true)
    private String introduction;
}
