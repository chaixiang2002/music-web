package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.Comment;
import com.example.yin.model.dto.CommentDto;
import com.example.yin.model.vo.CommentVo;

import java.util.List;

public interface CommentService extends IService<Comment> {

    int getCommentUp(Integer commentId);

    boolean updateComment(Integer commentId, int type);

    R addComment(CommentDto addCommentRequest);

    R updateCommentMsg(CommentDto upCommentRequest);

    R deleteComment(Integer id);

    R commentOfSongId(Integer songId);

    R commentOfSongListId(Integer songListId);

    List<CommentVo> getCommentList(List<CommentVo> comments);


}
