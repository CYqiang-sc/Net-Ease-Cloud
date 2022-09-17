package com.sicnu.cloudmusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sicnu.cloudmusic.entity.UserRole;
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
public interface UserRoleMapper extends BaseMapper<UserRole>
{
    /**
     * 插入用户
     * @param userId 用户id
     */
    @Insert("insert into user_role(user_id,role_id) values(#{userId},(select id from role where role_name = '用户'))")
    void insertUserRole(Integer userId);

    /**
     * 修改用户为管理员
     * @param userId 用户id
     */
    @Update("insert into user_role(user_id,role_id) values(#{userId},(select id from role where role_name = '管理员'))")
    void updateToAdmin(Integer userId);

    /**
     * 修改管理员为普通用户
     * @param userId 用户id
     */
    @Update("delete from user_role where user_id = #{userId} and role_id = (select id from role where role_name = '管理员')")
    void updateToUser(Integer userId);

    /**
     * 通过用户id查询对应的角色id
     * @param userId 用户id
     * @return 角色id列表
     */
    @Select("select role_id from user_role where user_id = #{userId}")
    List<Integer> select(Integer userId);
}
