package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.config.RedisTemplate;
import com.example.yin.common.Constants;
import com.example.yin.model.dto.*;
import com.example.yin.service.ConsumerService;
import com.example.yin.util.AuthUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@RestController
@Api(tags = "用户接口", value = "用户接口")
@Validated
public class ConsumerController {

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用户注册
     */
    @PostMapping("/user/add")
    @ApiOperation(tags = "用户注册", value = "用户注册")
    public R addUser(@RequestBody @Validated ConsumerRegistryDto registryRequest) {
        return consumerService.addUser(registryRequest);
    }

    /**
     * 登录判断
     */
    @PostMapping("/user/login/status")
    @ApiOperation(tags = "用户登录", value = "用户登录")
    public R loginStatus(@RequestBody @Validated UserLoginDto loginRequest) {
        return consumerService.loginStatus(loginRequest);
    }

    /**
     * 返回所有用户
     */
    @GetMapping("/user")
    @ApiOperation(tags = "查询全部用户信息", value = "查询全部用户信息")
    public R allUser() {
        return consumerService.allUser();
    }


    /**
     * 返回指定 ID 的用户
     */
    @GetMapping("/user/detail")
    @ApiOperation(tags = "获取某个用户的信息", value = "获取某个用户的信息")
    public R userOfId(@RequestParam @NotNull(message = "用户id不能为空") int id) {
        return consumerService.userOfId(id);
    }

    /**
     * 删除用户
     */
    @GetMapping("/user/delete")
    @ApiOperation(tags = "删除用户", value = "删除用户")
    public R deleteUser(@RequestParam @NotNull(message = "用户id不能为空") int id) {
        return consumerService.deleteUser(id);
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/user/update")
    @ApiOperation(tags = "更新用户信息", value = "更新用户信息")
    public R updateUserMsg(@RequestBody @Validated UserUpdateDto updateRequest) {
        return consumerService.updateUserMsg(updateRequest);
    }

    /**
     * 更新用户密码
     */
    @PostMapping("/user/updatePassword")
    @ApiOperation(tags = "修改用户密码", value = "修改用户密码")
    public R updatePassword(@RequestBody @Validated UpdatePasswordDto updatePasswordRequest) {
        return consumerService.updatePassword(updatePasswordRequest);
    }

    /**
     * 更新用户头像
     */
    @PostMapping("/user/avatar/update")
    @ApiOperation(tags = "更新用户头像", value = "更新用户头像")
    public R updateUserPic(@RequestParam("file") @NotNull(message = "用户头像文件不能为空")  MultipartFile avatorFile,
                           @RequestParam("id") @NotNull(message = "用户id不能为空") int id) {
        return consumerService.updateUserAvator(avatorFile, id);
    }

    @PostMapping("/user/logout")
    @ApiOperation(tags = "用户登出", value = "用户登出")
    public R userLogout() {
        Optional<UserRedisDto> loginUser = AuthUtil.getLoginUser(redisTemplate);
        //删掉用户在redis里面的信息代表退出
        if (loginUser.isPresent()) {
            redisTemplate.remove(Constants.TOKEN_PREFIX + AuthUtil.getToken(),
                    Constants.ID_PREFIX + "1:" + loginUser.get().getId());

        }
        return R.success("登出成功");
    }
}
