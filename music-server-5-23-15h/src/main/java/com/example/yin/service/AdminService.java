package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.Admin;
import com.example.yin.model.dto.AdminDto;

import javax.servlet.http.HttpSession;

public interface AdminService extends IService<Admin> {

    R verityPasswd(AdminDto adminRequest, HttpSession session);
}
