package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.common.Constants;
import com.example.yin.mapper.*;
import com.example.yin.model.domain.*;
import com.example.yin.model.dto.AddSongListDto;
import com.example.yin.model.dto.SongListDto;
import com.example.yin.service.SongListService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SongListServiceImpl extends ServiceImpl<SongListMapper, SongList> implements SongListService {

    @Autowired
    private SongListMapper songListMapper;


    @Autowired
    private ListSongMapper listSongMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private RankListMapper rankListMapper;

    @Autowired
    private UserSupportMapper userSupportMapper;


    @Override
    public R updateSongListMsg(SongListDto updateSongListRequest) {
        SongList songList = new SongList();
        BeanUtils.copyProperties(updateSongListRequest, songList);
        if (songListMapper.updateById(songList) > 0) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    @Override
    @Transactional
    public R deleteSongList(Integer id) {
        //1、删除歌单对应的歌单歌曲对应数据
        LambdaUpdateWrapper<ListSong> listSongLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        listSongLambdaUpdateWrapper.eq(ListSong::getSongListId, id);
        listSongMapper.delete(listSongLambdaUpdateWrapper);

        //2、删除歌单的评论   以及评论对应的点赞
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getSongListId, id);
        List<Comment> comments = commentMapper.selectList(commentLambdaQueryWrapper);
        if (!ObjectUtils.isEmpty(comments)) {
            List<Integer> collect = comments.stream().map(o -> o.getId()).collect(Collectors.toList());
            LambdaUpdateWrapper<UserSupport> userSupportLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userSupportLambdaUpdateWrapper.in(UserSupport::getCommentId, collect);
            userSupportMapper.delete(userSupportLambdaUpdateWrapper);

            commentMapper.deleteBatchIds(collect);
        }

        //3、删除歌单的评分
        LambdaUpdateWrapper<RankList> rankListLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        rankListLambdaUpdateWrapper.eq(RankList::getSongListId, id);
        rankListMapper.delete(rankListLambdaUpdateWrapper);

        SongList songList = songListMapper.selectById(id);

        //4、删除歌单的头像文件
        if (!songList.getPic().equals(Constants.BASE_SONG_LIST_IMAGE)) {
            String filePath = Constants.ASSETS_PATH + System.getProperty("file.separator") + songList.getPic();
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }

        //4、删除歌单
        if (songListMapper.deleteById(id) > 0) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    @Override
    public R allSongList() {
        return R.success(null, songListMapper.selectList(null));
    }

    @Override
    public R likeTitle(String title) {
        LambdaQueryWrapper<SongList> songListLambdaQueryWrapper = new LambdaQueryWrapper<>();
        songListLambdaQueryWrapper.like(SongList::getTitle, title);
        return R.success(null, songListMapper.selectList(songListLambdaQueryWrapper));
    }

    @Override
    public R likeStyle(String style) {
        LambdaQueryWrapper<SongList> songListLambdaQueryWrapper = new LambdaQueryWrapper<>();
        songListLambdaQueryWrapper.like(SongList::getStyle, style);
        return R.success(null, songListMapper.selectList(songListLambdaQueryWrapper));
    }

    @Override
    public R addSongList(AddSongListDto addSongListRequest) {
        SongList songList = new SongList();
        BeanUtils.copyProperties(addSongListRequest, songList);
        String pic = Constants.BASE_SONG_LIST_IMAGE;//默认图片
        songList.setPic(pic);
        if (songListMapper.insert(songList) > 0) {
            return R.success("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @Override
    public R updateSongListImg(MultipartFile avatorFile, @RequestParam("id") int id) {
        String suffix = null;
        if (ObjectUtils.isEmpty(avatorFile) || avatorFile.getSize() <= 0) {
            return R.error("文件不能为空");
        } else {
            String originalFilename = avatorFile.getOriginalFilename();
            int lastIndexOf = originalFilename.lastIndexOf(".");
            if (lastIndexOf != -1) {
                suffix = originalFilename.substring(lastIndexOf + 1, originalFilename.length());
                if (ObjectUtils.isEmpty(suffix)) return R.error("文件类型不符合,只能上传png、jpg、jpeg、gif文件");
                else {
                    if (!(suffix.equals("png") || suffix.equals("jpg")) || suffix.equals("jpeg") || suffix.equals("gif"))
                        return R.error("文件类型不符合,只能上传png、jpg、jpeg、gif文件");
                }
            } else {
                return R.error("文件类型不符合,只能上传png、jpg、jpeg、gif文件");
            }
        }

        String fileName = UUID.randomUUID() + "." + suffix;
        String filePath = Constants.ASSETS_PATH + System.getProperty("file.separator") + "img" + System.getProperty("file.separator") + "songListPic";
        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdir();
        }
        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        String imgPath = "/img/songListPic/" + fileName;
        //2、存文件
        try {
            avatorFile.transferTo(dest);
        } catch (IOException e) {
            return R.fatal("上传失败" + e.getMessage());
        }

        //3、更新歌单中的图片信息
        SongList songList = new SongList();
        songList.setId(id);
        songList.setPic(imgPath);
        if (songListMapper.updateById(songList) > 0) {
            return R.success("上传成功", imgPath);
        } else {
            return R.error("上传失败");
        }
    }
}
