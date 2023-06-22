package com.example.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yin.model.domain.ListSong;
import com.example.yin.model.domain.Song;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListSongMapper extends BaseMapper<ListSong> {

    List<Song> selectSongNotInListSong(@Param("songListId") Integer songListId);
}
