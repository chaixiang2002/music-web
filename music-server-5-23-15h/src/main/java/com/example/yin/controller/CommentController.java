package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.model.dto.CommentDto;
import com.example.yin.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@Api(tags = "用户评论接口", value = "用户评论接口")
@Validated
public class CommentController {
    @Autowired
    private CommentService commentService;


    // 提交评论
    @PostMapping("/comment/add")
    @ApiOperation(tags = "用户添加一条评论",
            value = "在用户评论表里面添加一条数据")
    public R addComment(@RequestBody @Validated CommentDto addCommentRequest) {
        return commentService.addComment(addCommentRequest);
    }

    // 删除评论
    @GetMapping("/comment/delete")
    @ApiOperation(tags = "用户删除一条评论",
            value = "在用户评价表里面删除一条数据")
    public R deleteComment(@RequestParam @NotNull(message = "评论id不能为空") Integer id) {
        return commentService.deleteComment(id);
    }

    // 获得指定歌曲 ID 的评论列表
    @GetMapping("/comment/song/detail")
    @ApiOperation(tags = "获取歌曲的评论列表",
            value = "获取歌曲的评论列表")
    public R commentOfSongId(@RequestParam @NotNull(message = "歌曲id不能为空") Integer songId) {
        return commentService.commentOfSongId(songId);
    }

    // 获得指定歌单 ID 的评论列表
    @GetMapping("/comment/songList/detail")
    @ApiOperation(tags = "获取歌单的评论列表",
            value = "获取歌单的评论列表")
    public R commentOfSongListId(@RequestParam @NotNull(message = "歌单id不能为空") Integer songListId) {
        return commentService.commentOfSongListId(songListId);
    }

    // 点赞
    @PostMapping("/comment/like")
    @ApiOperation(tags = "点赞评论",
            value = "点赞评论")
    public R commentOfLike(@RequestBody CommentDto upCommentRequest) {
        return commentService.updateCommentMsg(upCommentRequest);
    }
}
