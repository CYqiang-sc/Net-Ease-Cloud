package com.sicnu.cloudmusic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author 莲童的雨儿
 */
@Data
@TableName("user_role")
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserRole
{
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer roleId;
}
