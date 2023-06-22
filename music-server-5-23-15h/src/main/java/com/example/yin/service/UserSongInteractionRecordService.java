package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.model.domain.UserSongInteractionRecord;
import com.example.yin.model.domain.UserSupport;

public interface UserSongInteractionRecordService extends IService<UserSongInteractionRecord> {

    boolean updateOrAddDownlocalCount(Integer consumerId, Integer songId);
}
