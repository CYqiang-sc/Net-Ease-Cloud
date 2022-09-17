package com.sicnu.cloudmusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sicnu.cloudmusic.entity.Permission;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author 莲童的雨儿
 */
@Repository
@Mapper
public interface PermissionMapper extends BaseMapper<Permission>
{

}
