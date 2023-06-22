package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.Collect;
import com.example.yin.model.dto.CollectDto;

public interface CollectService extends IService<Collect> {


    R addCollection(CollectDto addCollectRequest);

    R existSongId(CollectDto isCollectRequest);

    R deleteCollect(Integer userId,Integer songId);

    R collectionOfUser(Integer userId);
}
