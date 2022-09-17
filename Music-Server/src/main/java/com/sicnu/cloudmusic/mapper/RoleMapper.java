package com.sicnu.cloudmusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sicnu.cloudmusic.entity.Role;
import org.apache.ibatis.annotations.Select;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author 莲童的雨儿
 */
@Repository
@Mapper
public interface RoleMapper extends BaseMapper<Role>
{

}
