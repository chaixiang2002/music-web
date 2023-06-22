package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.config.RedisTemplate;
import com.example.yin.mapper.CollectMapper;
import com.example.yin.mapper.SongMapper;
import com.example.yin.model.domain.Collect;
import com.example.yin.model.domain.LoginUser;
import com.example.yin.model.domain.Song;
import com.example.yin.model.dto.CollectDto;
import com.example.yin.model.dto.UserRedisDto;
import com.example.yin.service.CollectService;
import com.example.yin.util.AuthUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect> implements CollectService {
    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SongMapper songMapper;

    @Override
    public R addCollection(CollectDto addCollectRequest) {

        //3、新增一条记录
        Collect collect = new Collect();
        BeanUtils.copyProperties(addCollectRequest, collect); //左 -> 右
        if (collectMapper.insert(collect) > 0) {
            return R.success("收藏成功", true);
        } else {
            return R.error("收藏失败");
        }
    }

    @Override
    public R existSongId(CollectDto isCollectRequest) {

        Optional<UserRedisDto> loginUser = AuthUtil.getLoginUser(redisTemplate);
        if (loginUser.isPresent()) {
            LambdaQueryWrapper<Collect> collectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            collectLambdaQueryWrapper.eq(Collect::getUserId, isCollectRequest.getUserId())
                    .eq(Collect::getSongId, isCollectRequest.getSongId());

            //如果说查到数据，那么代表已收藏
            if (collectMapper.selectCount(collectLambdaQueryWrapper) > 0) {
                return R.success("已收藏", true);
            } else {
                return R.success("未收藏", false);
            }
        } else {
            return R.success("未收藏", false);
        }
    }

    @Override
    public R deleteCollect(Integer userId, Integer songId) {

        //3、删掉一条记录
        LambdaQueryWrapper<Collect> collectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectLambdaQueryWrapper.eq(Collect::getUserId, userId)
                .eq(Collect::getSongId, songId);

        //取消收藏就是删掉收藏咯
        if (collectMapper.delete(collectLambdaQueryWrapper) > 0) {
            return R.success("取消收藏", false);
        } else {
            return R.error("取消收藏失败");
        }
    }

    @Override
    public R collectionOfUser(Integer userId) {
        LambdaQueryWrapper<Collect> collectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectLambdaQueryWrapper.eq(Collect::getUserId, userId);
        List<Collect> collects = collectMapper.selectList(collectLambdaQueryWrapper);
        if (!ObjectUtils.isEmpty(collects)) {
            //拉出歌曲id
            List<Integer> collect = collects.stream().map(o -> o.getSongId()).collect(Collectors.toList());

            //查询歌曲信息
            List<Song> songs = songMapper.selectBatchIds(collect);
            return R.success("用户收藏", songs);
        }

        return R.success("用户收藏", null);
    }
}
