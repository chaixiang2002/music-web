package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.service.UserSupportService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @Author 祝英台炸油条
 * @Time : 2022/6/11 16:07
 **/
@RestController
@RequestMapping("/userSupport")
@Validated
public class UserSupportController {

    @Autowired
    UserSupportService userSupportService;


    //用户操作一条评论
    @PostMapping("/setSupport")
    public R setCommentSupport(@RequestParam @NotNull(message = "评论id不能空") Integer commentId) {
        return userSupportService.setCommentSupport(commentId);
    }


}
