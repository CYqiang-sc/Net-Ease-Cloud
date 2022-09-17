package com.sicnu.cloudmusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sicnu.cloudmusic.entity.RolePermission;
import org.apache.ibatis.annotations.Select;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 莲童的雨儿
 */
@Repository
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission>
{
    /**
     * 通过用户id查询用户权限id
     * @param userId 用户id
     * @return 权限id列表
     */
    @Select("select permission_id from role_permission where role_id in(select role_id from user_role where user_id = #{userId})")
    List<Integer> getPermissionIdByUserId(Integer userId);
}
