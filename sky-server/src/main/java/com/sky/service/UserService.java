package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService {

    /**
     * 用户登录
     *
     * @param userLoginDTO 用户登录信息
     * @return 登录成功的用户信息
     */
    User wxlogin(UserLoginDTO userLoginDTO);
}
