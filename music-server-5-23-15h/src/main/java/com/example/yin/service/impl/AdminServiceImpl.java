package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.config.RedisTemplate;
import com.example.yin.common.Constants;
import com.example.yin.mapper.AdminMapper;
import com.example.yin.model.domain.Admin;
import com.example.yin.model.dto.AdminDto;
import com.example.yin.model.dto.UserRedisDto;
import com.example.yin.service.AdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.UUID;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public R verityPasswd(AdminDto adminRequest, HttpSession session) {
        //1、先验证是否存在
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getName, adminRequest.getUsername());
        queryWrapper.eq(Admin::getPassword, adminRequest.getPassword());

        Admin admin = adminMapper.selectOne(queryWrapper);

        if (ObjectUtils.isEmpty(admin)) {
            return R.error("用户名或密码错误");
        } else {
            HashMap<String, Object> hashMap = new HashMap<>();

            //生成token
            String token = UUID.randomUUID().toString();



            UserRedisDto userRedisDto = new UserRedisDto();
            BeanUtils.copyProperties(admin, userRedisDto);
            userRedisDto.setType(0);

            //给前端的数据
            hashMap.put("token", token);
            hashMap.put("user", userRedisDto);

            // 把新的数据加入redis，存一个token->user
            redisTemplate.setObject(Constants.TOKEN_PREFIX + token, userRedisDto, Constants.TOKEN_TIME);

            //再存一个id->token
            redisTemplate.set(Constants.ID_PREFIX + "0:" + admin.getId(), token, Constants.TOKEN_TIME);


            return R.success("登录成功", hashMap);

        }

    }
}
