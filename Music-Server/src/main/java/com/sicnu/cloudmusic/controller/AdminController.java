package com.sicnu.cloudmusic.controller;

import com.sicnu.cloudmusic.annotation.CheckPermission;
import com.sicnu.cloudmusic.annotation.Log;
import com.sicnu.cloudmusic.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author 莲童的雨儿
 */
@Api(value = "AdminController",tags = {"用户、日志管理"})
@RestController
@Validated
public class AdminController
{
    private final AdminService adminService;
    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @RequestMapping(value = "/login/back",method = RequestMethod.GET)
    @ApiOperation("登录后台")
    @Log(value = "登录后台",encode = true)
    public String loginBack(@RequestParam(value = "phone" )@NotBlank String phone,
                            @RequestParam(value = "password") @NotBlank String password,
                            @RequestParam(value = "time") @NotBlank String time,
                            @RequestParam(value = "verifyCode") @NotBlank String verifyCode,
                            HttpServletRequest request) throws Exception
    {
        return adminService.loginBack(phone,password,time,verifyCode,request);
    }

    @RequestMapping(value = "/user/select/phone",method = RequestMethod.GET)
    @ApiOperation("通过手机号查询用户")
    @CheckPermission
    public String selectUserByPhone(@RequestParam(value = "phone") @NotBlank String phone){
        return adminService.searchUserByPhone(phone);
    }

    @RequestMapping(value = "/user/select/nickname",method = RequestMethod.GET)
    @ApiOperation("通过昵称查询用户")
    @CheckPermission
    public String selectUserByUsername(@RequestParam(value = "nickname") @NotBlank String nickname){
        return adminService.searchUserByUsername(nickname);
    }

    @RequestMapping(value = "/user/delete",method = RequestMethod.GET)
    @ApiOperation("删除用户")
    @Log("删除用户")
    @CheckPermission
    public String deleteUser(@RequestParam(value = "id") @NotNull Integer id){
        return adminService.deleteUserById(id);
    }

    @RequestMapping(value = "/user/ban",method = RequestMethod.GET)
    @ApiOperation("封禁用户")
    @Log("封禁用户")
    @CheckPermission
    public String banUser(@RequestParam(value = "id") @NotNull Integer id){
        return adminService.banUserById(id);
    }

    @RequestMapping(value = "/user/unseal",method = RequestMethod.GET)
    @ApiOperation("解封用户")
    @Log("解封用户")
    @CheckPermission
    public String unsealUser(@RequestParam(value = "id") @NotNull Integer id){
        return adminService.unsealUserById(id);
    }

    @RequestMapping(value = "/user/get/all",method = RequestMethod.GET)
    @ApiOperation("获取所有用户")
    @CheckPermission
    public String getAllUser(){
        return adminService.getUsers();
    }

    @RequestMapping(value = "/user/get/admin",method = RequestMethod.GET)
    @ApiOperation("获取管理员用户")
    @CheckPermission
    public String getAdmin(){
        return adminService.getAdmin();
    }

    @RequestMapping(value = "/user/set/admin",method = RequestMethod.GET)
    @ApiOperation("设置管理员")
    @Log("设置管理员")
    @CheckPermission
    public String setAdmin(@RequestParam(value = "id") @NotNull Integer id){
        return adminService.updateUserToAdmin(id);
    }

    @RequestMapping(value = "/user/delete/admin",method = RequestMethod.GET)
    @ApiOperation("删除管理员")
    @Log("删除管理员")
    @CheckPermission
    public String deleteAdmin(@RequestParam(value = "id") @NotNull Integer id){
        return adminService.updateAdminToUser(id);
    }

    @RequestMapping(value = "/log/all",method = RequestMethod.GET)
    @ApiOperation("获取所有日志")
    @CheckPermission
    public String getLogs(){
        return adminService.getLogs();
    }

    @RequestMapping(value = "/log/page",method = RequestMethod.GET)
    @ApiOperation("分页获取日志")
    @CheckPermission
    public String getPageLog(@RequestParam(value = "pageNum") @NotNull Integer pageNum,
                             @RequestParam(value = "pageSize") @NotNull Integer pageSize){
        return adminService.findPage(pageNum,pageSize);
    }

    @RequestMapping(value = "/log/select",method = RequestMethod.GET)
    @ApiOperation("查询日志")
    @CheckPermission
    public String getLog(@RequestParam(value = "pageNum") @NotNull Integer pageNum,
                         @RequestParam(value = "pageSize") @NotNull Integer pageSize,
                         @RequestParam(value = "startTime") @NotBlank String startTime,
                         @RequestParam(value = "endTime") @NotBlank String endTime){
        return adminService.getLog(pageNum,pageSize,startTime,endTime);
    }

    @RequestMapping(value = "/ban/ip",method = RequestMethod.GET)
    @ApiOperation("封禁ip")
    @Log("封禁ip")
    @CheckPermission
    public String banIp(@RequestParam(value = "ip") @NotBlank String ip){
        return adminService.banIp(ip);
    }

    @RequestMapping(value = "/unseal/ip",method = RequestMethod.GET)
    @ApiOperation("解封ip")
    @Log("解封ip")
    @CheckPermission
    public String unsealIp(@RequestParam(value = "ip") @NotBlank String ip){
        return adminService.unsealIp(ip);
    }

    @RequestMapping(value = "/message/get",method = RequestMethod.GET)
    @ApiOperation("获取消息")
    @CheckPermission
    public String getMessage(@RequestParam(value = "userId") @NotBlank String userId){
        return adminService.getMessage(userId);
    }

    @RequestMapping(value = "/message/publish",method = RequestMethod.GET)
    @ApiOperation("发布消息")
    @Log("发布消息")
    @CheckPermission
    public String publishMessage(@RequestParam(value = "msg") @NotBlank String msg){
        return adminService.publishMessage(msg);
    }

    @RequestMapping(value = "/message/read",method = RequestMethod.GET)
    @ApiOperation("设置消息已读")
    @Log("消息已读")
    @CheckPermission
    public String setAlRead(@RequestParam(value = "userId") @NotNull Integer userId,
                            @RequestParam(value = "msgId") @NotNull Integer msgId){
        return adminService.setAlRead(userId, msgId);
    }
}
