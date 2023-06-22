package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface ConsumerService extends IService<Consumer> {

    R addUser(ConsumerRegistryDto registryRequest);

    R updateUserMsg(UserUpdateDto updateRequest);

    R updateUserAvator(MultipartFile avatorFile, int id);

    R updatePassword(UpdatePasswordDto updatePasswordRequest);

    boolean existUserName(String username);

    boolean existUserEmail(String email);

    boolean existUserPhone(String phone);


    boolean verityPasswd(String username, String password);

    R deleteUser(Integer id);

    R allUser();

    R userOfId(Integer id);

    R loginStatus(UserLoginDto loginRequest);

}
