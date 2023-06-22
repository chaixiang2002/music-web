package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.common.Constants;
import com.example.yin.mapper.SingerMapper;
import com.example.yin.model.domain.Singer;
import com.example.yin.model.dto.SingerDto;
import com.example.yin.service.SingerService;
import com.example.yin.service.SongService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class SingerServiceImpl extends ServiceImpl<SingerMapper, Singer> implements SingerService {

    @Resource
    private SingerMapper singerMapper;

    @Resource
    private SongService songService;

    @Override
    public R updateSingerMsg(SingerDto updateSingerRequest) {
        //1、没有id则不修改了
        if (ObjectUtils.isEmpty(updateSingerRequest.getId())) {
            return R.error("修改失败");
        }
        Singer singer = new Singer();
        BeanUtils.copyProperties(updateSingerRequest, singer);

        if (singerMapper.updateById(singer) > 0) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    @Override
    public R updateSingerPic(MultipartFile avatorFile, int id) {
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

        //1、处理一下文件名称，前面加个当前时间的时间戳
        String fileName = UUID.randomUUID() + "." + suffix;
        String filePath = Constants.ASSETS_PATH + System.getProperty("file.separator") + "img" + System.getProperty("file.separator") + "singerPic";
        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdir();
        }

        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        String imgPath = "/img/singerPic/" + fileName;
        //2、存文件
        try {
            avatorFile.transferTo(dest);
        } catch (IOException e) {
            return R.fatal("上传失败" + e.getMessage());
        }

        //3、更新歌手中的图片信息
        Singer singer = new Singer();
        singer.setId(id);
        singer.setPic(imgPath);
        if (singerMapper.updateById(singer) > 0) {
            return R.success("上传成功", imgPath);
        } else {
            return R.error("上传失败");
        }
    }

    // 删除歌手
    @Override
    @Transactional
    public R deleteSinger(Integer id) {
        //1、删除该歌手歌曲中的歌曲信息
        boolean flag = songService.deleteSongBySinger(id);

        //2、删除歌手的头像文件
        Singer singer = singerMapper.selectById(id);

        //不是默认头像才删除
        if (!singer.getPic().equals(Constants.USER_BASE_IMAGE)) {
            String filePath = Constants.ASSETS_PATH + singer.getPic();
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }


        //3、删除 歌手信息
        if (singerMapper.deleteById(id) > 0) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    @Override
    public R allSinger() {
        return R.success(null, singerMapper.selectList(null));
    }

    @Override
    public R addSinger(SingerDto addSingerRequest) {
        Singer singer = new Singer();
        BeanUtils.copyProperties(addSingerRequest, singer);
        String pic = Constants.USER_BASE_IMAGE;
        singer.setPic(pic);
        if (singerMapper.insert(singer) > 0) {
            return R.success("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @Override
    public R singerOfName(String name) {
        LambdaQueryWrapper<Singer> singerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        singerLambdaQueryWrapper.like(Singer::getName, name);
        return R.success(null, singerMapper.selectList(singerLambdaQueryWrapper));
    }

    @Override
    public R singerOfSex(Byte sex) {
        LambdaQueryWrapper<Singer> singerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        singerLambdaQueryWrapper.eq(Singer::getSex, sex);
        return R.success(null, singerMapper.selectList(singerLambdaQueryWrapper));
    }
}
