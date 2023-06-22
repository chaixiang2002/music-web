package com.example.yin.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdateSongListDto {


    String songIds;

    @NotNull(message = "歌单id不能为空")
    Integer songListId;
}
