package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.config.RedisTemplate;
import com.example.yin.mapper.RankListMapper;
import com.example.yin.model.domain.RankList;
import com.example.yin.model.dto.RankListDto;
import com.example.yin.model.dto.UserRedisDto;
import com.example.yin.service.RankListService;
import com.example.yin.util.AuthUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

/**
 * @author Administrator
 */
@Service
public class RankListServiceImpl extends ServiceImpl<RankListMapper, RankList> implements RankListService {


    @Autowired
    private RankListMapper rankMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public R addorUpdateRank(RankListDto rankListAddRequest) {

        //1。有则修改，无则评分
        LambdaUpdateWrapper<RankList> rankListLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        rankListLambdaUpdateWrapper.eq(RankList::getConsumerId, rankListAddRequest.getConsumerId())
                .eq(RankList::getSongListId, rankListAddRequest.getSongListId());

        RankList one = rankMapper.selectOne(rankListLambdaUpdateWrapper);

        //2、空则添加评价
        if (ObjectUtils.isEmpty(one)) {
            RankList rankList = new RankList();
            BeanUtils.copyProperties(rankListAddRequest, rankList);
            if (rankMapper.insert(rankList) > 0) {
                return R.success("评价成功");
            } else {
                return R.error("评价失败");
            }
        }
        //3、非空则修改评分
        else {
            RankList rankList = new RankList();
            BeanUtils.copyProperties(rankListAddRequest, rankList);
            rankList.setId(one.getId());
            int i = rankMapper.updateById(rankList);
            if (i > 0) {
                return R.success("评价成功");
            } else {
                return R.error("评价失败");
            }
        }
    }

    @Override
    public R rankOfSongListId(Long songListId) {
        //计算在sql语句里面了
        int score = rankMapper.selectScoreSum(songListId);
        return R.success(null, score);
    }

    @Override
    public R getUserRank(Long consumerId, Long songListId) {
        Optional<UserRedisDto> loginUser = AuthUtil.getLoginUser(redisTemplate);
        if (loginUser.isPresent()) {
            Integer integer = rankMapper.selectUserRank(consumerId, songListId);
            if (ObjectUtils.isEmpty(integer)) return R.success(null, null);
            else
                return R.success(null, integer);
        } else {
            return R.success(null, null);
        }
    }
}
