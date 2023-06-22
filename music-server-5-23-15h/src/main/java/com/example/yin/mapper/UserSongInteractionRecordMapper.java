package com.example.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yin.model.domain.UserSongInteractionRecord;
import com.example.yin.model.dto.SongMulScoreDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSongInteractionRecordMapper extends BaseMapper<UserSongInteractionRecord> {

  List<SongMulScoreDto> getSongScore();
}
