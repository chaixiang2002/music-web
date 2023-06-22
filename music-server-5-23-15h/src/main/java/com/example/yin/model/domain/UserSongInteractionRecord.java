package com.example.yin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "user_song_interaction_record")
@Data
public class UserSongInteractionRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer consumerId;
    private Integer songId;
    private Long downloadCount;
    private Long playbackCount;

}
