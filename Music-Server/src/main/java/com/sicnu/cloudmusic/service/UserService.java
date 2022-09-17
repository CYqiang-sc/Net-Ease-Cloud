package com.sicnu.cloudmusic.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sicnu.cloudmusic.common.Result;
import com.sicnu.cloudmusic.entity.*;
import com.sicnu.cloudmusic.mapper.*;
import com.sicnu.cloudmusic.properties.CloudMusicProperties;
import com.sicnu.cloudmusic.util.DesUtil;
import com.sicnu.cloudmusic.util.IpUtil;
import com.sicnu.cloudmusic.util.UriUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 莲童的雨儿
 */
@Service
public class UserService
{
    private final UserMapper userMapper;

    private final UserRoleMapper userRoleMapper;

    private final BanIpMapper banIpMapper;

    private final UriUtil uriUtil;

    private final RestTemplate restTemplate;

    DesUtil desUtil = new DesUtil();

    @Autowired
    public UserService(UserMapper userMapper,
                       UserRoleMapper userRoleMapper,
                       BanIpMapper banIpMapper,
                       UriUtil uriUtil,
                       RestTemplate restTemplate){
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.banIpMapper = banIpMapper;
        this.uriUtil = uriUtil;
        this.restTemplate = restTemplate;
    }

    private final CloudMusicProperties cloudMusicProperties = new CloudMusicProperties();

    /**
     *
     * @param phone 手机号
     * @param password 密码
     * @return 返回信息
     */
    public String login(String phone,
                        String password,
                        HttpServletRequest request) throws Exception
    {
        String ip = IpUtil.getIpAddr(request);
        QueryWrapper<BanIp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ip",ip);
        BanIp banIp = banIpMapper.selectOne(queryWrapper);
        if (banIp != null){
            return JSON.toJSONString(new Result<>().setCode(0).setMsg("该ip已被封禁"));
        }
        User user = userMapper.selectUserByPhone(phone);
        if (user != null){
            if (user.getStatus() == 1)
            {
                if (!desUtil.encrypt(password).equals(user.getPassword())){
                    return JSON.toJSONString(new Result<>().setCode(0).setMsg("密码错误"));
                }
                return JSON.toJSONString(new Result<>().setCode(1).setMsg("登录成功"));
            }
            else if (user.getStatus() == 0){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("该账号已被封禁"));
            }
            else if (user.getStatus() == -1){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("不存在该用户"));
            }
        }
        else {
            if (!existence(phone)){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("不存在该用户"));
            }
            JSONObject jsonObject = toLogin(phone, password);
            String code = jsonObject.getString("code");
            String success = "200";
            if (success.equals(code)){
                user = new User()
                        .setUsername(jsonObject.getJSONObject("profile").getString("nickname"))
                        .setPhone(phone)
                        .setPassword(desUtil.encrypt(password))
                        .setStatus(1)
                        .setProfileUrl(jsonObject.getJSONObject("profile").getString("avatarUrl"))
                        .setRegisterTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                userMapper.insertUser(user);
                User user1 = userMapper.selectUserByPhone(phone);
                userRoleMapper.insertUserRole(user1.getId());
                return JSON.toJSONString(new Result<>().setCode(1).setMsg("注册成功"));
            }
            else {
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("注册失败"));
            }
        }
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("系统错误"));
    }

    /**
     * 一键登录
     * @param appId 微信app id
     * @param request 请求
     * @return 登录信息
     * @throws Exception 加密解密异常
     */
    public String login(String appId, HttpServletRequest request) throws Exception
    {
        String ip = IpUtil.getIpAddr(request);
        QueryWrapper<BanIp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ip",ip);
        BanIp banIp = banIpMapper.selectOne(queryWrapper);
        if (banIp != null){
            return JSON.toJSONString(new Result<>().setCode(0).setMsg("该ip已被封禁"));
        }
        User user = userMapper.selectUserByAppId(appId);
        if (user != null){
            if (user.getStatus() == 0){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("该账号已被封禁"));
            }
            else if (user.getStatus() == -1){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("不存在该用户"));
            }
            User user1 = new User().setPhone(user.getPhone()).setPassword(desUtil.decrypt(user.getPassword()));
            return JSON.toJSONString(new Result<>().setCode(1).setMsg("登录成功").setData(user1));
        }
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("请先绑定您的账号"));
    }

    /**
     * 绑定用户app id
     * @param appId 微信app id
     * @param phone 手机号
     * @param password 密码
     * @return 绑定信息
     * @throws Exception 加密解密异常
     */
    public String bind(String appId,
                       String phone,
                       String password) throws Exception
    {
        User user = userMapper.selectUserByPhone(phone);
        if (user != null){
            if (desUtil.encrypt(password).equals(user.getPassword())){
                userMapper.updateAppIdById(appId, user.getId());
                return JSON.toJSONString(new Result<>().setCode(1).setMsg("绑定成功"));
            }
            return JSON.toJSONString(new Result<>().setCode(0).setMsg("密码错误"));
        }else {
            if (!existence(phone)){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("不存在该用户"));
            }
            JSONObject jsonObject = toLogin(phone, password);
            String code = jsonObject.getString("code");
            String success = "200";
            if (success.equals(code)){
                user = new User()
                        .setUsername(jsonObject.getJSONObject("profile").getString("nickname"))
                        .setPhone(phone)
                        .setPassword(desUtil.encrypt(password))
                        .setStatus(1)
                        .setProfileUrl(jsonObject.getJSONObject("profile").getString("avatarUrl"))
                        .setAppId(appId)
                        .setRegisterTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                userMapper.insertUser(user);
                User user1 = userMapper.selectUserByPhone(phone);
                userRoleMapper.insertUserRole(user1.getId());
                return JSON.toJSONString(new Result<>().setCode(1).setMsg("绑定成功"));
            }
            else {
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("绑定失败"));
            }
        }
    }
    /**
     * 网易云登录接口
     * @param phone 手机号
     * @param password 密码
     * @return 返回登录后信息
     */
    public JSONObject toLogin(String phone,
                              String password){
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("phone", phone);
        params.add("password", password);
        String url = uriUtil.getUrl(cloudMusicProperties,"loginUrl",params);
        String forEntity = restTemplate.getForObject(url, String.class);
        return JSON.parseObject(forEntity);
    }

    /**
     * 检查是否已存在该账号
     * @param phone 手机号
     * @return 返回是否存在
     */
    public Boolean existence(String phone)
    {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("phone",phone);
        String url = uriUtil.getUrl(cloudMusicProperties,"existenceUrl",params);
        String forEntity = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(forEntity);
        return "1".equals(jsonObject.getString("exist"));
    }
}
