package com.example.yin.model.dto;

import com.example.yin.annotation.ByteEnumRule;
import com.example.yin.annotation.RegularRule;
import com.example.yin.annotation.StringEnumRule;
import com.example.yin.annotation.StringLengthRule;
import lombok.Data;

import java.util.Date;

@Data
public class ConsumerRegistryDto {


    @RegularRule(rule = "[0-9a-zA-Z]{6,12}", message = "用户名仅能由大小写字母和数字组成，并且长度是6~12位,不能为空", notNull = true)
    private String username;


    @RegularRule(rule = "[0-9a-zA-Z]{6,12}", message = "密码仅能由大小写字母和数字组成，并且长度是6~12位,不能为空", notNull = true)
    private String password;

    @ByteEnumRule(rule = "0,1,2", message = "用户性别只有0，1，2三种", notNull = true)
    private Byte sex;

    @RegularRule(rule = "^1[3,4,5,6,7,8,9][0-9]{9}$", message = "手机号格式不合法")
    private String phoneNum;


    @RegularRule(rule = "^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", message = "邮箱格式不合法")
    private String email;


    private Date birth;

    @StringLengthRule(min = 0, max = 30, message = "用户自我介绍不能多于30个字哦")
    private String introduction;

    @StringEnumRule(rules = "北京,天津,河北,山西,内蒙古,辽宁,吉林,黑龙江,上海,江苏,浙江,安徽,福建," +
            "江西,山东,河南,湖北,湖南,广东,广西,海南,重庆,四川,贵州,云南,西藏,陕西,甘肃,青海,宁夏,新疆,香港,澳门,台湾",
            message = "歌单类型只能在规定范围内进行选择哦,不能为空", notNull = false)
    private String location;


}
