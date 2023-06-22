package com.example.yin.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CommentVo {
    private Integer id;
    private Integer userId;

    private String userName;

    private String avator;

    private Integer songId;

    private Integer songListId;

    private String content;

    private Byte type;

    private Date createTime;

    private Integer up;

    private boolean isSupport;

}
