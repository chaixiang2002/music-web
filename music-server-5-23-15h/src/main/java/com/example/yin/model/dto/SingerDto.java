package com.example.yin.model.dto;

import com.example.yin.annotation.ByteEnumRule;
import com.example.yin.annotation.StringEnumRule;
import com.example.yin.annotation.StringLengthRule;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author 祝英台炸油条
 * @Time : 2022/6/6 20:14
 **/
@Data
public class SingerDto {

    private Integer id;

    @StringLengthRule(min = 1, max = 10, message = "歌手名称长度在1~10,不能为空", notNull = true)
    private String name;

    @ByteEnumRule(rule = "0,1,2", message = "歌手性别只能是0-女 1-男 2-组合 ,不能为空", notNull = true)
    private Byte sex;

    @NotNull(message = "歌手出生日期不能为空")
    private Date birth;


    @StringEnumRule(rules = "中国," +
            "韩国,意大利,新加坡,美国," +
            "西班牙,日本", message = "歌手故乡只能在规定范围内进行选择哦,不能为空", notNull = true)
    private String location;


    @StringLengthRule(min = 10, max = 250, message = "歌手介绍数据范围是10到250字,不能为空", notNull = true)
    private String introduction;
}
