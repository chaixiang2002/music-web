package com.example.yin.model.dto;

import com.example.yin.annotation.StringLengthRule;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author 祝英台炸油条
 * @Time : 2022/6/6 14:29
 **/
@Data
public class CommentDto {
    private Integer id;

    @NotNull
    private Integer userId;

    private Integer songId;

    private Integer songListId;


    @StringLengthRule(min = 1,max = 45,notNull = true,message = "评论长度在1~45,不能为空")
    private String content;

    private Date createTime;

    private Byte nowType;

    private Integer up;//点赞
}
