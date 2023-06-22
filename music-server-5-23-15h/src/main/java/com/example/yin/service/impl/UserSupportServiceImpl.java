package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.config.RedisTemplate;
import com.example.yin.mapper.UserSupportMapper;
import com.example.yin.model.domain.UserSupport;
import com.example.yin.model.dto.UserRedisDto;
import com.example.yin.model.dto.UserSupportDto;
import com.example.yin.model.vo.ChangeSupportVo;
import com.example.yin.service.CommentService;
import com.example.yin.service.UserSupportService;
import com.example.yin.util.AuthUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author asus
 * @description 针对表【user_support】的数据库操作Service实现
 * @createDate 2022-06-11 16:06:28
 */
@Service
public class UserSupportServiceImpl extends ServiceImpl<UserSupportMapper, UserSupport>
        implements UserSupportService {

    @Resource
    private UserSupportMapper userSupportMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private CommentService commentService;

    @Override
    @Transactional
    public R setCommentSupport(Integer commentId) {
        //1、先查一下用户是否已点赞
        Optional<UserRedisDto> loginUser = AuthUtil.getLoginUser(redisTemplate);
        UserRedisDto consumer = loginUser.get();

        LambdaQueryWrapper<UserSupport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserSupport::getCommentId, commentId);
        queryWrapper.eq(UserSupport::getUserId, consumer.getId());

        UserSupport userSupport = userSupportMapper.selectOne(queryWrapper);

        ChangeSupportVo changeSupportVo = new ChangeSupportVo();


        //2、无说明是点赞
        if (ObjectUtils.isEmpty(userSupport)) {
            changeSupportVo.setState(1);
            userSupport = new UserSupport();
            userSupport.setUserId(consumer.getId());
            userSupport.setCommentId(commentId);

            int insert = userSupportMapper.insert(userSupport);
            if (insert < 0) {
                throw new RuntimeException("插入用户点赞信息异常");
            }

            //让评论数+1
            commentService.updateComment(commentId, 1);
        }

        //3、有则说明是取消赞
        else {
            changeSupportVo.setState(0);
            int delete = userSupportMapper.delete(queryWrapper);
            if (delete < 0) {
                throw new RuntimeException("删除用户点赞信息异常");
            }

            //让评论数-1
            commentService.updateComment(commentId, -1);
        }

        int commentUp = commentService.getCommentUp(commentId);
        changeSupportVo.setUp(commentUp);

        return R.success("成功", changeSupportVo);
    }

    @Override
    public R isUserSupportComment(UserSupportDto userSupportRequest) {
        //1、先查一下用户是否已点赞
        QueryWrapper<UserSupport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comment_id", userSupportRequest.getCommentId());
        queryWrapper.eq("user_id", userSupportRequest.getUserId());
        //2、如果原来有点赞信息
        if (userSupportMapper.selectCount(queryWrapper) > 0) {
            return R.success("您已取消点赞", true);
        } else {
            return R.success("您已点赞", false);
        }
    }

    @Override
    public R insertCommentSupport(UserSupportDto userSupportRequest) {
        UserSupport userSupport = new UserSupport();
        BeanUtils.copyProperties(userSupportRequest, userSupport);
        if (userSupportMapper.insert(userSupport) > 0) {
            return R.success("添加记录成功");
        }
        return R.error("添加时,发生异常");
    }

    @Override
    public R deleteCommentSupport(UserSupportDto userSupportRequest) {
        QueryWrapper<UserSupport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comment_id", userSupportRequest.getCommentId());
        queryWrapper.eq("user_id", userSupportRequest.getUserId());
        if (userSupportMapper.delete(queryWrapper) > 0) {
            return R.success("删除记录成功");
        }
        return R.error("删除记录发生异常");
    }
}
