package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.UserSupport;
import com.example.yin.model.dto.UserSupportDto;

/**
 * @author asus
 * @description 针对表【user_support】的数据库操作Service
 * @createDate 2022-06-11 16:06:28
 */
public interface UserSupportService extends IService<UserSupport> {

    R setCommentSupport(Integer commentId);
    R isUserSupportComment(UserSupportDto userSupportRequest);

    R insertCommentSupport(UserSupportDto userSupportRequest);

    R deleteCommentSupport(UserSupportDto userSupportRequest);
}
