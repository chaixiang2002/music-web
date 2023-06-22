package com.example.yin.service;

import com.example.yin.model.dto.SongScoreDto;

import java.util.HashMap;
import java.util.List;

public interface RecommendService {
    public List<SongScoreDto> recommendSongList();

    public List<SongScoreDto> popularityRecommendation(int n, List<Integer> excludeSongIds);

    public HashMap<Integer, HashMap<Integer, Integer>> getScoreTable();


    public List<SongScoreDto> randomRecommendation(int n, List<Integer> excludeSongIds);
}
