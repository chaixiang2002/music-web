package com.example.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yin.model.domain.Comment;
import com.example.yin.model.vo.CommentVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMapper extends BaseMapper<Comment> {

    int updateUp(@Param("commentId") Integer id,@Param("change") Integer change);


    List<CommentVo> getSongCommentList(@Param("songId") Integer songId);

    List<CommentVo> getSongListCommentList(@Param("songListId") Integer songListId);
}
