package com.example.yin.model.dto;

import lombok.Data;

@Data
public class SongMulScoreDto {
    private Integer songId;
    private Double downloadScore;
    private Double playbackScore;

}
