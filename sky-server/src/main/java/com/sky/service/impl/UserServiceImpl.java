package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    // 微信服务接口地址
    public static String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     *
     * @param userLoginDTO 用户登录信息
     * @return 登录成功的用户信息
     */
    @Override
    public User wxlogin(UserLoginDTO userLoginDTO) {

        String openid = getOpenid(userLoginDTO.getCode());

        // 判断openid是否为空，如果为空，登录失败，抛出业务异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 判断当前用户是否为新用户
        User user = userMapper.selectByOpenid(openid);

        // 如果是新用户，注册用户信息到数据库
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        // 返回用户信息
        return user;
    }

    /**
     * 调用微信接口，获取用户的openid
     *
     * @param code 微信登录时获取的临时code
     * @return 用户的openid
     */
    private String getOpenid (String code) {
        // 调用微信接口服务，获取当前用户的openid
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString("openid");
    }
}
