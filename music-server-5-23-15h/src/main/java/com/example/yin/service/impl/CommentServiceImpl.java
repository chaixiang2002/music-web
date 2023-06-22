package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.config.RedisTemplate;
import com.example.yin.mapper.CommentMapper;
import com.example.yin.mapper.UserSupportMapper;
import com.example.yin.model.domain.*;
import com.example.yin.model.dto.CommentDto;
import com.example.yin.model.dto.UserRedisDto;
import com.example.yin.model.vo.CommentVo;
import com.example.yin.service.AdminService;
import com.example.yin.service.CommentService;
import com.example.yin.service.ConsumerService;
import com.example.yin.util.AuthUtil;
import com.example.yin.util.SensitiveUtil;
import com.example.yin.util.SensitiveWordCheckUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private SensitiveWordCheckUtils sensitiveWordCheckUtils;

    @Resource
    private UserSupportMapper userSupportMapper;

    @Override
    public int getCommentUp(Integer commentId) {
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.select(Comment::getUp).eq(Comment::getId, commentId);
        Comment comment = commentMapper.selectOne(commentLambdaQueryWrapper);
        return comment.getUp();
    }

    @Override
    @Transactional
    public boolean updateComment(Integer commentId, int type) {

        Comment comment = commentMapper.selectById(commentId);
        if (!ObjectUtils.isEmpty(comment)) {
            //保证数据库的完整性  不能存一个-1进去
            if (type == -1 && comment.getUp() == 0) {
                commentMapper.updateUp(commentId, 0);
            }
            commentMapper.updateUp(commentId, type);
        }

        return false;
    }

    @Override
    public R addComment(CommentDto addCommentRequest) {
        Integer songId = addCommentRequest.getSongId();
        Integer songListId = addCommentRequest.getSongListId();
        if (ObjectUtils.isEmpty(songId) && ObjectUtils.isEmpty(songListId)) {
            return R.success("评论失败");
        }

        //1、获取当前项目路径
//        String s = SensitiveUtil.filterString(addCommentRequest.getContent());
        Set<String> sensitiveWordMaxMatch = sensitiveWordCheckUtils.getSensitiveWordMaxMatch(addCommentRequest.getContent());
        if (sensitiveWordMaxMatch.size() > 0) {
            return R.error("评论有敏感词~");
        }

        addCommentRequest.setContent(addCommentRequest.getContent());

        //3、插入一条数据
        Comment comment = new Comment();
        BeanUtils.copyProperties(addCommentRequest, comment);
        comment.setType(addCommentRequest.getNowType());
        if (commentMapper.insert(comment) > 0) {
            return R.success("评论成功");
        } else {
            return R.error("评论失败");
        }
    }

    @Override
    public R updateCommentMsg(CommentDto addCommentRequest) {
        //评论信息的点赞
        Comment comment = new Comment();

        BeanUtils.copyProperties(addCommentRequest, comment);
        if (comment.getUp() < 0) comment.setUp(0);

        //如果插入成功
        if (commentMapper.updateById(comment) > 0) {

            return R.success("点赞成功");
        } else {
            return R.error("点赞失败");
        }
    }

    //    删除评论
    @Override
    @Transactional
    public R deleteComment(Integer id) {
        //1、前面判断完了，这里删除一下就行了
        //1.1、删掉评论对应的点赞信息
        LambdaQueryWrapper<UserSupport> userSupportLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userSupportLambdaQueryWrapper.eq(UserSupport::getCommentId, id);
        int delete = userSupportMapper.delete(userSupportLambdaQueryWrapper);

        //1.2、删掉评论
        int delete1 = commentMapper.deleteById(id);

        if (delete1 > 0) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    @Override
    public R commentOfSongId(Integer songId) {
        //1、查询当前歌曲评论列表
        List<CommentVo> songCommentList = commentMapper.getSongCommentList(songId);
        if (songCommentList.size() > 0)
            return R.success("", getCommentList(songCommentList));
        else return R.success(null, null);
    }

    @Override
    public R commentOfSongListId(Integer songListId) {
        //1、查询当前歌单评论列表
        List<CommentVo> songListCommentList = commentMapper.getSongListCommentList(songListId);
        if (songListCommentList.size() > 0)
            return R.success("", getCommentList(songListCommentList));
        else return R.success(null, null);
    }



    public List<CommentVo> getCommentList(List<CommentVo> comments) {
        //1、获取当前用户登录信息
        Optional<UserRedisDto> loginUserOptional = AuthUtil.getLoginUser(redisTemplate);

        //2、当前有用户登录
        if (loginUserOptional.isPresent()) {
            UserRedisDto loginUser = loginUserOptional.get();
            Integer id = loginUser.getId();

            //获取当前用户的全部评论记录
            LambdaQueryWrapper<UserSupport> userSupportLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userSupportLambdaQueryWrapper.eq(UserSupport::getUserId, id);
            List<UserSupport> userSupports = userSupportMapper.selectList(userSupportLambdaQueryWrapper);

            //false代表还没点赞，true代表已点赞

            comments.stream().forEach(
                    o -> {
                        //当前评论用户是登录用户
                        if (o.getUserId() == id) {
                            boolean flag = false;
                            if (!ObjectUtils.isEmpty(userSupports)) {
                                for (UserSupport userSupport : userSupports) {
                                    Integer commentId = userSupport.getCommentId();

                                    //如果该用户已经点赞过了，就不能点赞了
                                    if (commentId.equals(o.getId())) {
                                        flag = true;
                                        break;
                                    }
                                }
                            }

                            o.setSupport(flag);
                        } else {
                            o.setSupport(false);
                        }
                    }
            );
        }
        //3、当前无用户登录
        else {
            comments.stream().forEach(o -> o.setSupport(true));
        }

        return comments;
    }
}
