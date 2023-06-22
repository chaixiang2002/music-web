package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.mapper.ListSongMapper;
import com.example.yin.model.domain.ListSong;
import com.example.yin.model.domain.Song;
import com.example.yin.model.dto.ListSongDto;
import com.example.yin.service.ListSongService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ListSongServiceImpl extends ServiceImpl<ListSongMapper, ListSong> implements ListSongService {

    @Autowired
    private ListSongMapper listSongMapper;

    @Override
    @Transactional
    public R updateSongInSongList(List<Integer> songIds, Integer songListId) {
        //1、首先删除掉该歌单原来的歌曲
        LambdaUpdateWrapper<ListSong> listSongLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        listSongLambdaUpdateWrapper.eq(ListSong::getSongListId, songListId);
        int delete = listSongMapper.delete(listSongLambdaUpdateWrapper);


        //2、插入新的歌曲
        if (!ObjectUtils.isEmpty(songIds) && songIds.size() > 0) {
            ArrayList<ListSong> listSongs = new ArrayList<>();
            songIds.stream().forEach(
                    o -> {
                        ListSong listSong = new ListSong();
                        listSong.setSongListId(songListId);
                        listSong.setSongId(o);
                        listSongs.add(listSong);
                    }
            );
            boolean b = this.saveBatch(listSongs);
            return b ? R.success("更新歌单成功", "更新歌单成功") : R.error("更新歌单失败");
        }
        return R.success("更新歌单成功", "更新歌单成功");
    }

    @Override
    public List<ListSong> allListSong() {
        return listSongMapper.selectList(null);
    }


    @Override
    public R getSongNotInSongList(Integer songListId) {
        List<Song> songs = listSongMapper.selectSongNotInListSong(songListId);
        return R.success(null, songs);
    }


    @Override
    public R updateListSongMsg(ListSongDto updateListSongRequest) {
        ListSong listSong = new ListSong();
        BeanUtils.copyProperties(updateListSongRequest, listSong);
        if (listSongMapper.updateById(listSong) > 0) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    @Override
    public R deleteListSong(Integer songId, Integer songListId) {
        LambdaUpdateWrapper<ListSong> listSongLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        listSongLambdaUpdateWrapper.eq(ListSong::getSongId, songId);
        listSongLambdaUpdateWrapper.eq(ListSong::getSongListId, songListId);
        if (listSongMapper.delete(listSongLambdaUpdateWrapper) > 0) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    @Override
    public R addListSong(ListSongDto addListSongRequest) {
        ListSong listSong = new ListSong();
        BeanUtils.copyProperties(addListSongRequest, listSong);
        if (listSongMapper.insert(listSong) > 0) {
            return R.success("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @Override
    public R listSongOfSongId(Integer songListId) {
        LambdaQueryWrapper<ListSong> listSongLambdaQueryWrapper = new LambdaQueryWrapper<>();
        listSongLambdaQueryWrapper.eq(ListSong::getSongListId, songListId);
        return R.success("查询成功", listSongMapper.selectList(listSongLambdaQueryWrapper));
    }


}
