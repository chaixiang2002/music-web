package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.mapper.UserSongInteractionRecordMapper;
import com.example.yin.mapper.UserSupportMapper;
import com.example.yin.model.domain.UserSongInteractionRecord;
import com.example.yin.model.domain.UserSupport;
import com.example.yin.service.UserSongInteractionRecordService;
import com.example.yin.service.UserSupportService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

@Service
public class UserSongInteractionRecordServiceImpl extends ServiceImpl<UserSongInteractionRecordMapper, UserSongInteractionRecord>
        implements UserSongInteractionRecordService {
    @Resource
    private UserSongInteractionRecordMapper userSongInteractionRecordMapper;


    @Override
    public boolean updateOrAddDownlocalCount(Integer consumerId, Integer songId) {
        //查询用户歌曲交互记录
        LambdaQueryWrapper<UserSongInteractionRecord> userSongInteractionRecordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userSongInteractionRecordLambdaQueryWrapper.eq(UserSongInteractionRecord::getSongId, songId)//歌曲id
                .eq(UserSongInteractionRecord::getConsumerId, consumerId);//用户id
        UserSongInteractionRecord userSongInteractionRecord = userSongInteractionRecordMapper.selectOne(userSongInteractionRecordLambdaQueryWrapper);

        //1、空则添加
        if (ObjectUtils.isEmpty(userSongInteractionRecord)) {
            UserSongInteractionRecord userSongInteractionRecord1 = new UserSongInteractionRecord();
            userSongInteractionRecord1.setSongId(songId);
            userSongInteractionRecord1.setConsumerId(consumerId);
            userSongInteractionRecord1.setDownloadCount(1L);
            userSongInteractionRecord1.setPlaybackCount(0L);
            int insert = userSongInteractionRecordMapper.insert(userSongInteractionRecord1);
            if (insert < 0) return false;
        }

        //2、否则就加1
        else {
            userSongInteractionRecord.setDownloadCount(userSongInteractionRecord.getDownloadCount() + 1);
            int update = userSongInteractionRecordMapper.updateById(userSongInteractionRecord);
            if (update < 0) return false;
        }

        return true;
    }
}
