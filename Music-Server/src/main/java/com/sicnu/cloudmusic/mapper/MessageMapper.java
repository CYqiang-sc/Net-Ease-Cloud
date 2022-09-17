package com.sicnu.cloudmusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sicnu.cloudmusic.entity.Message;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author 莲童的雨儿
 */
@Mapper
@Repository
public interface MessageMapper extends BaseMapper<Message>
{

}
