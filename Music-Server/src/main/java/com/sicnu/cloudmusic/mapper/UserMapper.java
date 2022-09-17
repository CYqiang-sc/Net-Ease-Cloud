package com.sicnu.cloudmusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sicnu.cloudmusic.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @author 莲童的雨儿
 */
@Repository
@Mapper
public interface UserMapper extends BaseMapper<User>
{
    /**
     * 插入用户
     * @param user 用户信息
     */
    @Insert("insert into user(username,phone,password,status,profile_url,register_time,app_id) values(#{username},#{phone},#{password},#{status},#{profileUrl},#{registerTime},#{appId})")
    void insertUser(User user);

    /**
     * 根据用户id修改用户app id
     * @param appId 微信app id
     * @param id 用户id
     */
    @Update("update user set app_id = #{appId} where id = #{id}")
    void updateAppIdById(String appId, Integer id);

    /**
     * 查询用户
     * @param phone 手机号
     * @return 返回一个用户
     */
    @Select("select * from user where phone = #{phone}")
    User selectUserByPhone(String phone);

    /**
     * 更新用户状态
     * @param phone 手机号
     * @param status 用户状态
     */
    @Update("update user set status = #{status} where phone = #{phone}")
    void updateUserStatusByPhone(String phone, Integer status);

    /**
     * 查询所有用户
     * @return 所有用户信息（除了密码）
     */
    @Select("select * from user where status != -1")
    List<User> selectUsers();

    /**
     * 通过id查询用户
     * @param id 用户id
     * @return 用户信息
     */
    @Select("select * from user where id = #{id}")
    User selectUserById(Integer id);

    /**
     * 通过app id查询用户
     * @param appId 微信app id
     * @return 用户信息
     */
    @Select("select * from user where app_id = #{appId}")
    User selectUserByAppId(String appId);
    /**
     * 通过用户名查询用户
     * @param username 用户名
     * @return 用户列表
     */
    @Select("select * from user where username like '%${username}%'")
    List<User> selectUserByUsername(String username);

    /**
     * 通过手机号查询用户
     * @param phone 手机号
     * @return 用户列表
     */
    @Select("select * from user where phone like '%${phone}%'")
    List<User> selectUsersByPhone(String phone);

    /**
     * 查询所有管理员用户
     * @return 管理员用户列表
     */
    @Select("select * from user where id in (select user_id from user_role where role_id = (select id from role where role_name = '管理员'))")
    List<User> selectAdmin();
}
