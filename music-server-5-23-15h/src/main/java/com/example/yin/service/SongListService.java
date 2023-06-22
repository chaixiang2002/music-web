package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.SongList;
import com.example.yin.model.dto.AddSongListDto;
import com.example.yin.model.dto.SongListDto;
import org.springframework.web.multipart.MultipartFile;

public interface SongListService extends IService<SongList> {

    R addSongList(AddSongListDto addSongListRequest);

    R updateSongListMsg(SongListDto updateSongListRequest);

    R updateSongListImg(MultipartFile avatorFile, int id);

    R deleteSongList(Integer id);

    R allSongList();

    R likeTitle(String title);

    R likeStyle(String style);
}
