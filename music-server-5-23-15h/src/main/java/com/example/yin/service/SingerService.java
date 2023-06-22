package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.Singer;
import com.example.yin.model.dto.SingerDto;
import org.springframework.web.multipart.MultipartFile;

public interface SingerService extends IService<Singer> {

    R addSinger(SingerDto addSingerRequest);

    R updateSingerMsg(SingerDto updateSingerRequest);

    R updateSingerPic(MultipartFile avatorFile, int id);

    R deleteSinger(Integer id);

    R allSinger();

    R singerOfName(String name);

    R singerOfSex(Byte sex);
}
