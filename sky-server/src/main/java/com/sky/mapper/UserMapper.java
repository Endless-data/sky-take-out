package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户信息
     *
     * @param openid 微信用户唯一标识
     * @return 用户信息
     */
    @Select("select * from user where openid = #{openid}")
    User selectByOpenid(String openid);

    /**
     * 插入用户信息
     *
     * @param user 用户信息
     */
    void insert(User user);

    /**
     * 根据用户id查询用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);
}
