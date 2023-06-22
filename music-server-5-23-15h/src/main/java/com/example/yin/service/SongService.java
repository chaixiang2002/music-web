package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.Song;
import com.example.yin.model.dto.AddSongDto;
import com.example.yin.model.dto.UpDateSongInfoDto;
import org.springframework.web.multipart.MultipartFile;

public interface SongService extends IService<Song> {

    R addSongPlayBackCount(Integer consumerId, Integer songId);

    R addSong (AddSongDto addSongRequest, MultipartFile mpfile);

    R updateSongMsg(UpDateSongInfoDto updateSongRequest);

    R updateSongUrl(MultipartFile urlFile, int id);

    R updateSongPic(MultipartFile urlFile, int id);

    R deleteSong(Integer id);

    boolean deleteSongBySinger(Integer singerId);

    R allSong();

    R songOfSingerId(Integer singerId);

    R songOfId(Integer id);

    R songOfSingerName(String name);
}
