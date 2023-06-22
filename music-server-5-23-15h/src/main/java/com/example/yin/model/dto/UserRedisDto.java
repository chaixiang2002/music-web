package com.example.yin.model.dto;

import lombok.Data;

@Data
public class UserRedisDto {
    private Integer id;

    private String username;

    private int type;//0-管理员  1-用户

    private String avator;
}
