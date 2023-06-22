package com.example.yin.model.dto;

import com.example.yin.annotation.RegularRule;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdatePasswordDto {

    @NotNull(message = "用户id不能为空")
    private Integer id;

    @NotNull(message = "用户名不能为空")
    @RegularRule(rule = "[0-9a-zA-Z]{6,12}", message = "用户名仅能由大小写字母和数字组成，并且长度是6~12位")
    private String username;

    @NotNull(message = "旧密码不能为空")
    @RegularRule(rule = "[0-9a-zA-Z]{6,12}", message = "密码仅能由大小写字母和数字组成，并且长度是6~12位")
    private String oldPassword;

    @NotNull(message = "新密码不能为空")
    @RegularRule(rule = "[0-9a-zA-Z]{6,12}", message = "密码仅能由大小写字母和数字组成，并且长度是6~12位")
    private String password;
}
