package com.sicnu.cloudmusic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotBlank;

/**
 * @author 莲童的雨儿
 */
@Data
@TableName("user")
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class User
{
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "用户名为必填项")
    private String username;

    @NotBlank(message = "手机号为必填项")
    private String phone;

    @NotBlank(message = "密码为必填项")
    private String password;

    /**
     * status: 0封禁 1正常 -1被删除
     */
    @NotBlank(message = "状态码为必填项")
    private Integer status;

    /**
     * 用户角色
     */
    private Integer role;

    /**
     * 注册时间
     */
    private String registerTime;

    /**
     * 头像url
     */
    private String profileUrl;

    /**
     * 微信app id
     */
    private String appId;
}
