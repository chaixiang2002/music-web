package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.Constants;
import com.example.yin.common.R;
import com.example.yin.mapper.*;
import com.example.yin.model.domain.*;
import com.example.yin.model.dto.AddSongDto;
import com.example.yin.model.dto.UpDateSongInfoDto;
import com.example.yin.service.SongService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements SongService {

    @Resource
    private SongMapper songMapper;

    @Resource
    private UserSongInteractionRecordMapper userSongInteractionRecordMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private ListSongMapper listSongMapper;

    @Resource
    private UserSupportMapper userSupportMapper;

    @Override
    public R allSong() {
        return R.success(null, songMapper.selectList(null));
    }

    @Override
    public R addSongPlayBackCount(Integer consumerId, Integer songId) {
        //查询该用户对该歌曲的交互记录
        LambdaQueryWrapper<UserSongInteractionRecord> userSongInteractionRecordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userSongInteractionRecordLambdaQueryWrapper.eq(UserSongInteractionRecord::getSongId, songId)
                .eq(UserSongInteractionRecord::getConsumerId, consumerId);
        UserSongInteractionRecord userSongInteractionRecord = userSongInteractionRecordMapper.selectOne(userSongInteractionRecordLambdaQueryWrapper);

        //1、空则添加
        if (ObjectUtils.isEmpty(userSongInteractionRecord)) {
            UserSongInteractionRecord userSongInteractionRecord1 = new UserSongInteractionRecord();
            userSongInteractionRecord1.setSongId(songId);
            userSongInteractionRecord1.setConsumerId(consumerId);
            userSongInteractionRecord1.setDownloadCount(0L);
            userSongInteractionRecord1.setPlaybackCount(1L);
            int insert = userSongInteractionRecordMapper.insert(userSongInteractionRecord1);
            if (insert < 0) return R.error("添加用户歌曲播放记录失败");
        }

        //2、否则就修改
        else {
            userSongInteractionRecord.setPlaybackCount(userSongInteractionRecord.getPlaybackCount() + 1);
            int update = userSongInteractionRecordMapper.updateById(userSongInteractionRecord);
            if (update < 0) return R.error("添加用户歌曲播放记录失败");
        }

        return R.success("添加成功");
    }

    @Override
    public R addSong(AddSongDto addSongRequest, MultipartFile mpfile) {
        String suffix = null;
        if (ObjectUtils.isEmpty(mpfile) || mpfile.getSize() <= 0) {
            return R.error("文件不能为空");
        } else {
            String originalFilename = mpfile.getOriginalFilename();
            int lastIndexOf = originalFilename.lastIndexOf(".");
            if (lastIndexOf != -1) {
                suffix = originalFilename.substring(lastIndexOf + 1, originalFilename.length());
                if (ObjectUtils.isEmpty(suffix)) return R.error("文件类型不符合,只能上传mp3文件或者mp4文件");
                else {
                    if (!(suffix.equals("mp3") || suffix.equals("m4a")))
                        return R.error("文件类型不符合,只能上传mp3文件或者mp4文件");
                }
            } else {
                return R.error("文件类型不符合,只能上传mp3文件或者mp4文件");
            }
        }

        Song song = new Song();
        BeanUtils.copyProperties(addSongRequest, song);
        String pic = Constants.SONG_IMAGE; //默认图片地址

        String fileName = UUID.randomUUID() + "." + suffix;
        String filePath = Constants.ASSETS_PATH + System.getProperty("file.separator") + "song" + System.getProperty("file.separator");
        File file1 = new File(filePath);
        if (!file1.exists()) {
            if (!file1.mkdir()) {
                return R.fatal("创建文件失败");
            }
        }
        File dest = new File(filePath + fileName);
        String storeUrlPath = "/song/" + fileName;
        try {
            mpfile.transferTo(dest);
        } catch (IOException e) {
            return R.fatal("上传失败" + e.getMessage());
        }
        song.setCreateTime(new Date());
        song.setUpdateTime(new Date());
        song.setPic(pic);
        song.setUrl(storeUrlPath);
        if (songMapper.insert(song) > 0) {
            return R.success("上传成功", storeUrlPath);
        } else {
            return R.error("上传失败");
        }
    }

    @Override
    public R updateSongMsg(UpDateSongInfoDto updateSongRequest) {
        Song song = new Song();
        BeanUtils.copyProperties(updateSongRequest, song);
        if (songMapper.updateById(song) > 0) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    @Override
    public R updateSongUrl(MultipartFile urlFile, int id) {

        String suffix = null;

        if (ObjectUtils.isEmpty(urlFile) || urlFile.getSize() <= 0) {
            return R.error("文件不能为空");
        } else {
            String originalFilename = urlFile.getOriginalFilename();
            int lastIndexOf = originalFilename.lastIndexOf(".");
            if (lastIndexOf != -1) {
                suffix = originalFilename.substring(lastIndexOf + 1, originalFilename.length());
                if (ObjectUtils.isEmpty(suffix)) return R.error("文件类型不符合,只能上传mp3文件或者mp4文件");
                else {
                    if (!(suffix.equals("mp3") || suffix.equals("m4a")))
                        return R.error("文件类型不符合,只能上传mp3文件或者mp4文件");
                }
            } else {
                return R.error("文件类型不符合,只能上传mp3文件或者mp4文件");
            }
        }

        String fileName = UUID.randomUUID() + "." + suffix;
        String filePath = Constants.ASSETS_PATH + System.getProperty("file.separator") + "song" + System.getProperty("file.separator");
        File file1 = new File(filePath);
        if (!file1.exists()) {
            if (!file1.mkdir()) {
                return R.fatal("创建目的文件夹失败");
            }
        }
        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        String storeUrlPath = "/song/" + fileName;
        try {
            urlFile.transferTo(dest);
        } catch (IOException e) {
            return R.fatal("更新失败" + e.getMessage());
        }

        //3、更新歌曲中的资源信息
        Song song = new Song();
        song.setId(id);
        song.setUrl(storeUrlPath);
        if (songMapper.updateById(song) > 0) {
            return R.success("更新成功", storeUrlPath);
        } else {
            return R.error("更新失败");
        }
    }

    @Override
    public R updateSongPic(MultipartFile urlFile, int id) {
        String suffix = null;
        if (ObjectUtils.isEmpty(urlFile) || urlFile.getSize() <= 0) {
            return R.error("文件不能为空");
        } else {
            String originalFilename = urlFile.getOriginalFilename();
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

        //1、处理一下文件名称，前面加个当前时间的时间戳
        String fileName = UUID.randomUUID() + "." + suffix;
        String filePath = Constants.ASSETS_PATH + System.getProperty("file.separator") + "img" + System.getProperty("file.separator") + "songPic";
        File file1 = new File(filePath);
        if (!file1.exists()) {
            if (!file1.mkdir()) {
                return R.fatal("创建文件夹失败");
            }
        }
        //2、存文件
        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        String storeUrlPath = "/img/songPic/" + fileName;
        try {
            urlFile.transferTo(dest);
        } catch (IOException e) {
            return R.fatal("上传失败" + e.getMessage());
        }
        //3、更新歌单中的图片信息
        Song song = new Song();
        song.setId(id);
        song.setPic(storeUrlPath);
        if (songMapper.updateById(song) > 0) {
            return R.success("上传成功", storeUrlPath);
        } else {
            return R.error("上传失败");
        }
    }

    // 删除歌曲
    @Override
    @Transactional
    public R deleteSong(Integer id) {

        //1、先找出这首歌对应的评论集合
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getSongId, id);
        List<Comment> comments = commentMapper.selectList(commentLambdaQueryWrapper);

        //1.1、如果这首歌有评论信息
        if (!ObjectUtils.isEmpty(comments)) {
            //1.2、那么先删除评论对应的点赞,取出评论的id 集合
            List<Integer> collect = comments.stream().map(o -> o.getId()).collect(Collectors.toList());
            LambdaUpdateWrapper<UserSupport> userSupportLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userSupportLambdaUpdateWrapper.in(UserSupport::getCommentId, collect);
            userSupportMapper.delete(userSupportLambdaUpdateWrapper);

            //1.3、再删除评论信息
            commentMapper.deleteBatchIds(collect);
        }

        //2、删除歌单中含有这些歌曲的记录
        LambdaUpdateWrapper<ListSong> listSongLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        listSongLambdaUpdateWrapper.eq(ListSong::getSongId, id);
        listSongMapper.delete(listSongLambdaUpdateWrapper);

        //3、删除用户歌曲交互记录
        LambdaUpdateWrapper<UserSongInteractionRecord> userSongInteractionRecordLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userSongInteractionRecordLambdaUpdateWrapper.eq(UserSongInteractionRecord::getSongId, id);
        userSongInteractionRecordMapper.delete(userSongInteractionRecordLambdaUpdateWrapper);

        //4、删除歌曲文件
        Song song = songMapper.selectById(id);
        String filePath = Constants.ASSETS_PATH + System.getProperty("file.separator") + song.getUrl();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        //5、删除歌曲头像文件
        if (!song.getPic().equals(Constants.SONG_IMAGE)) {
            filePath = Constants.ASSETS_PATH + System.getProperty("file.separator") + song.getPic();
            file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }


        //6、删除歌曲
        if (songMapper.deleteById(id) > 0) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    @Override
    @Transactional
    public boolean deleteSongBySinger(Integer singerId) {
        //查询这个歌手有全部歌曲
        LambdaQueryWrapper<Song> songLambdaQueryWrapper = new LambdaQueryWrapper<>();
        songLambdaQueryWrapper.eq(Song::getSingerId, singerId);

        List<Song> songs = songMapper.selectList(songLambdaQueryWrapper);

        for (Song song : songs) {
            Integer songId = song.getId();
            //1、先找出这首歌对应的评论集合
            LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            commentLambdaQueryWrapper.eq(Comment::getSongId, songId);
            List<Comment> comments = commentMapper.selectList(commentLambdaQueryWrapper);

            //1.1、如果这首歌有评论信息
            if (!ObjectUtils.isEmpty(comments)) {
                //1.2、那么先删除评论对应的点赞,取出评论的id 集合
                List<Integer> collect = comments.stream().map(o -> o.getId()).collect(Collectors.toList());
                LambdaUpdateWrapper<UserSupport> userSupportLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                userSupportLambdaUpdateWrapper.in(UserSupport::getCommentId, collect);
                userSupportMapper.delete(userSupportLambdaUpdateWrapper);

                //1.3、再删除评论信息
                int i = commentMapper.deleteBatchIds(collect);
                if (i <= 0) return false;
            }

            //2、删除歌单中含有这些歌曲的记录
            LambdaUpdateWrapper<ListSong> listSongLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            listSongLambdaUpdateWrapper.eq(ListSong::getSongId, songId);
            listSongMapper.delete(listSongLambdaUpdateWrapper);

            //3、删除用户歌曲交互记录
            LambdaUpdateWrapper<UserSongInteractionRecord> userSongInteractionRecordLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userSongInteractionRecordLambdaUpdateWrapper.eq(UserSongInteractionRecord::getSongId, songId);
            userSongInteractionRecordMapper.delete(userSongInteractionRecordLambdaUpdateWrapper);

            //4、删除歌曲文件
            Song song1 = songMapper.selectById(songId);
            String filePath = Constants.ASSETS_PATH + System.getProperty("file.separator") + song1.getUrl();
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }

            //5、删除歌曲头像文件
            if (!song.getPic().equals("/img/songPic/tubiao.jpg")) {
                filePath = Constants.ASSETS_PATH + System.getProperty("file.separator") + song1.getPic();
                file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            }

            //6、删除歌曲
            int i = songMapper.deleteById(songId);
            if (i <= 0) return false;

        }
        return true;
    }

    @Override
    public R songOfSingerId(Integer singerId) {
        LambdaQueryWrapper<Song> songLambdaQueryWrapper = new LambdaQueryWrapper<>();
        songLambdaQueryWrapper.eq(Song::getSingerId, singerId);
        return R.success(null, songMapper.selectList(songLambdaQueryWrapper));
    }

    @Override
    public R songOfId(Integer id) {
        LambdaQueryWrapper<Song> songLambdaQueryWrapper = new LambdaQueryWrapper<>();
        songLambdaQueryWrapper.eq(Song::getId, id);
        return R.success(null, songMapper.selectList(songLambdaQueryWrapper));
    }

    @Override
    public R songOfSingerName(String name) {
        LambdaQueryWrapper<Song> songLambdaQueryWrapper = new LambdaQueryWrapper<>();
        songLambdaQueryWrapper.like(Song::getName, name);
        return R.success(null, songMapper.selectList(songLambdaQueryWrapper));
    }
}
