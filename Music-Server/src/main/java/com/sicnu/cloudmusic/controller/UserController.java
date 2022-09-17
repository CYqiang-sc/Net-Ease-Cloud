package com.sicnu.cloudmusic.controller;

import com.sicnu.cloudmusic.annotation.Log;
import com.sicnu.cloudmusic.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * @author 莲童的雨儿
 */
@Api(value = "UserController",tags = {"用户登录"})
@RestController
@Validated
public class UserController
{
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/login/cellphone",method = RequestMethod.GET)
    @ApiOperation("登录")
    @Log(value = "用户登录",encode = true)
    public String login(@RequestParam(value = "phone") @NotBlank String phone,
                        @RequestParam(value = "password") @NotBlank String password,
                        HttpServletRequest request) throws Exception
    {
        return userService.login(phone,password,request);
    }

    @RequestMapping(value = "/login/appId",method = RequestMethod.GET)
    @ApiOperation("一键登录")
    @Log(value = "用户一键登录")
    public String loginByAppId(@RequestParam(value = "appId") @NotBlank String appId,
                               HttpServletRequest request) throws Exception
    {
        return userService.login(appId,request);
    }

    @RequestMapping(value = "/login/bind",method = RequestMethod.GET)
    @ApiOperation("绑定appId")
    @Log(value = "用户绑定appId",encode = true)
    public String bind(@RequestParam(value = "appId") @NotBlank String appId,
                       @RequestParam(value = "password") @NotBlank String password,
                       @RequestParam(value = "phone") @NotBlank String phone
                       ) throws Exception
    {
        return userService.bind(appId,phone,password);
    }
}
