package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.RankList;
import com.example.yin.model.dto.RankListDto;

public interface RankListService extends IService<RankList> {

    R addorUpdateRank(RankListDto rankListAddRequest);

    R rankOfSongListId(Long songListId);

    R getUserRank(Long consumerId, Long songListId);

}
