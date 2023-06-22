package com.example.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yin.model.domain.Collect;
import com.example.yin.model.dto.SongCollectionDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectMapper extends BaseMapper<Collect> {

    List<SongCollectionDto> getSongCollectCount();
}
