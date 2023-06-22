package com.example.yin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class LoginUser {

    private Integer id;

    private String username;
}
