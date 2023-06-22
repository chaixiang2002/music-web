package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.config.RedisTemplate;
import com.example.yin.common.Constants;
import com.example.yin.model.dto.AdminDto;
import com.example.yin.model.dto.UserRedisDto;
import com.example.yin.service.AdminService;
import com.example.yin.util.AuthUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * 后台管理的相关事宜
 */
@RestController
@Api(tags = "管理员接口", value = "管理员登录用的")
@Validated
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisTemplate redisTemplate;

    // 管理员登录接口
    @PostMapping("/admin/login/status")
    @ApiOperation(tags = "管理员登录", value = "管理员登录")
    public R loginStatus(@RequestBody @Validated AdminDto adminRequest, HttpSession session) {
        return adminService.verityPasswd(adminRequest, session);
    }

    @PostMapping("/admin/logout")
    @ApiOperation(tags = "管理员登出", value = "管理员登出")
    public R adminLogout() {
        Optional<UserRedisDto> loginUser = AuthUtil.getLoginUser(redisTemplate);
        //删掉用户在redis里面的信息代表退出
        if (loginUser.isPresent()) {
            redisTemplate.remove(Constants.TOKEN_PREFIX + AuthUtil.getToken(),
                    Constants.ID_PREFIX + "0:" + loginUser.get().getId());
        }
        return R.success("登出成功");
    }
}
