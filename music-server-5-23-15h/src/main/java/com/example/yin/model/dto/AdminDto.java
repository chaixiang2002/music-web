package com.example.yin.model.dto;

import com.example.yin.annotation.RegularRule;
import lombok.Data;

/**
 * @Author 祝英台炸油    条
 * @Time : 2022/6/6 18:44
 **/
@Data
public class AdminDto {

    @RegularRule(rule = "[0-9a-zA-Z]{6,12}", message = "用户名仅能由大小写字母和数字组成，并且长度是6~12位,不能为空",notNull = true)
    private String username;

    @RegularRule(rule = "[0-9a-zA-Z]{6,12}", message = "密码仅能由大小写字母和数字组成，并且长度是6~12位,不能为空",notNull = true)
    private String password;
}
