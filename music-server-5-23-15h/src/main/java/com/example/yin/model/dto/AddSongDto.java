package com.example.yin.model.dto;

import com.example.yin.annotation.ByteSizeRule;
import com.example.yin.annotation.StringLengthRule;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class AddSongDto {
    @NotNull(message = "歌手id不能为空")
    private Integer singerId;


    @StringLengthRule(min = 1, max = 30, message = "歌曲名称范围是1~30，且不能为空", notNull = true)
    private String name;

    @StringLengthRule(min = 1, max = 150, message = "歌曲介绍长度为1~150，且不能为空", notNull = true)
    private String introduction;

    @ByteSizeRule(message = "歌词字节长度65535字节，存不下哦", notNull = true)
    private String lyric;



}
