package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.ListSong;
import com.example.yin.model.dto.ListSongDto;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ListSongService extends IService<ListSong> {

    R addListSong(ListSongDto addListSongRequest);

    R updateListSongMsg(ListSongDto updateListSongRequest);

    R deleteListSong(Integer songId, Integer songListId);

    //看看这啥
    List<ListSong> allListSong();

    R listSongOfSongId(Integer songListId);

    //获取那些不在歌单中的歌曲信息
    R getSongNotInSongList(Integer songListId);

    //更新歌单中的歌曲
    R updateSongInSongList(List<Integer> songIds,Integer songListId);
}
