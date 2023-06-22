package com.example.yin.model.dto;

import com.example.yin.model.domain.Song;
import lombok.Data;

@Data
public class SongScoreDto {
    private Song song;
    private double score;
}
