package com.sicnu.cloudmusic.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sicnu.cloudmusic.common.Result;
import com.sicnu.cloudmusic.entity.*;
import com.sicnu.cloudmusic.mapper.*;
import com.sicnu.cloudmusic.util.DesUtil;
import com.sicnu.cloudmusic.util.IpUtil;
import com.sicnu.cloudmusic.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * @author 莲童的雨儿
 */
@Service
public class AdminService
{
    private final UserMapper userMapper;

    private final RoleMapper roleMapper;

    private final UserRoleMapper userRoleMapper;

    private final PermissionMapper permissionMapper;

    private final RolePermissionMapper rolePermissionMapper;

    private final LogMapper logMapper;

    private final BanIpMapper banIpMapper;

    private final MessageMapper messageMapper;

    private final AlReadMapper alReadMapper;

    private final StringRedisTemplate redisTemplate;

    private final DesUtil desUtil = new DesUtil();

    @Autowired
    public AdminService(UserMapper userMapper,
                        RoleMapper roleMapper,
                        UserRoleMapper userRoleMapper,
                        PermissionMapper permissionMapper,
                        RolePermissionMapper rolePermissionMapper,
                        LogMapper logMapper,
                        BanIpMapper banIpMapper,
                        MessageMapper messageMapper,
                        AlReadMapper alReadMapper,
                        StringRedisTemplate redisTemplate){
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.permissionMapper = permissionMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.logMapper = logMapper;
        this.banIpMapper = banIpMapper;
        this.messageMapper = messageMapper;
        this.alReadMapper = alReadMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 管理员登录后台
     * @param phone 手机号
     * @param password 密码
     * @return 登录成功与否
     */
    public String loginBack(String phone,
                            String password,
                            String time,
                            String verifyCode,
                            HttpServletRequest request) throws Exception
    {
        if (verifyCode != null){
            String code;
            try
            {
                code = Objects.requireNonNull(redisTemplate.opsForValue().get(time));
                redisTemplate.delete(time);
            } catch (NullPointerException e){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("请刷新验证码"));
            }
            if (!code.equalsIgnoreCase(verifyCode)){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("验证码错误"));
            }
        }
        QueryWrapper<BanIp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ip",IpUtil.getIpAddr(request));
        BanIp banIp = banIpMapper.selectOne(queryWrapper);
        if (banIp != null){
            return JSON.toJSONString(new Result<>().setCode(0).setMsg("该ip已被封禁"));
        }
        User user = userMapper.selectUserByPhone(phone);
        if (user != null){
            if (user.getStatus() == 0){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("该账号已被禁用"));
            }
            else if (user.getStatus() == -1){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("不存在该账号"));
            }
            QueryWrapper<Role> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("role_name","管理员");
            Integer roleId = roleMapper.selectOne(queryWrapper1).getId();
            List<Integer> roles = userRoleMapper.select(user.getId());
            if (!roles.contains(roleId)){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("不是管理员账号"));
            }
            else if (desUtil.encrypt(password).equals(user.getPassword())){
                List<Integer> permissionId = rolePermissionMapper.getPermissionIdByUserId(user.getId());
                QueryWrapper<Permission> permissionQueryWrapper = new QueryWrapper<>();
                permissionQueryWrapper.in("id",permissionId);
                List<Permission> permissions = permissionMapper.selectList(permissionQueryWrapper);
                List<String> permissionList = new ArrayList<>();
                permissions.forEach(permission -> permissionList.add(permission.getPermissionName()));
                String token = TokenUtil.getToken(phone,password);
                redisTemplate.opsForValue().set(token,permissionList.toString());
                Common<String> common = new Common<>();
                common.setId(user.getId());
                common.setCommon(token);
                return JSON.toJSONString(new Result<>().setCode(1).setMsg("管理员登录成功").setData(common));
            }
            else {
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("密码错误"));
            }
        }
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("不存在该账号"));
    }

    /**
     * 通过手机号查询用户
     * @param phone 手机号
     * @return 用户列表
     */
    public String searchUserByPhone(String phone){
        List<User> users = userMapper.selectUsersByPhone(phone);
        List<Integer> admins = new ArrayList<>();
        userMapper.selectAdmin().forEach(admin -> admins.add(admin.getId()));
        for (User user:users
             )
        {
            user.setPassword(null);
            if (admins.contains(user.getId())){
                user.setRole(2);
            }
            else {
                user.setRole(1);
            }
        }
        return JSON.toJSONString(new Result<>().setCode(1).setMsg("通过手机号查询用户成功").setData(users));
    }

    /**
     * 通过用户名查询用户
     * @param username 用户名
     * @return 用户列表
     */
    public String searchUserByUsername(String username){
        List<Integer> admins = new ArrayList<>();
        userMapper.selectAdmin().forEach(admin -> admins.add(admin.getId()));
        List<User> users = userMapper.selectUserByUsername(username);
        users.forEach(user -> {
            user.setPassword(null);
            if (admins.contains(user.getId())){
                user.setRole(2);
            }
            else {
                user.setRole(1);
            }
        });
        return JSON.toJSONString(new Result<>().setCode(1).setMsg("通过用户名查询用户成功").setData(users));
    }

    /**
     * 删除用户
     * @param id 用户id
     * @return 删除成功与否
     */
    public String deleteUserById(Integer id){
        User user = userMapper.selectUserById(id);
        if (user != null){
            if (user.getStatus() == -1){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("不存在该用户"));
            }
            QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_name","超级管理员");
            Integer roleId = roleMapper.selectOne(queryWrapper).getId();
            List<Integer> roles = userRoleMapper.select(user.getId());
            System.out.println(roles);
            if (roles.contains(roleId)){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("这是超级管理员，您不能删除他"));
            }
            userMapper.updateUserStatusByPhone(user.getPhone(),-1);
            return JSON.toJSONString(new Result<>().setCode(1).setMsg("删除用户成功"));
        }
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("不存在该用户"));
    }

    /**
     * 封禁用户
     * @param id 用户id
     * @return 封禁成功与否
     */
    public String banUserById(Integer id){
        User user = userMapper.selectUserById(id);
        if (user != null){
            Integer status = user.getStatus();
            if (status == 1){
                QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("role_name","超级管理员");
                Integer roleId = roleMapper.selectOne(queryWrapper).getId();
                List<Integer> roles = userRoleMapper.select(user.getId());
                if (roles.contains(roleId)){
                    return JSON.toJSONString(new Result<>().setCode(0).setMsg("这是超级管理员，您不能封禁他"));
                }
                userMapper.updateUserStatusByPhone(user.getPhone(),0);
                return JSON.toJSONString(new Result<>().setCode(1).setMsg("封禁用户成功"));
            }
            else if (status == 0){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("该用户已被封禁"));
            }
        }
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("不存在该用户"));
    }

    /**
     * 解封用户
     * @param id 用户id
     * @return 解封成功与否
     */
    public String unsealUserById(Integer id){
        User user = userMapper.selectUserById(id);
        if (user != null){
            Integer status = user.getStatus();
            if (status == 0){
                userMapper.updateUserStatusByPhone(user.getPhone(),1);
                return JSON.toJSONString(new Result<>().setCode(1).setMsg("解封用户成功"));
            }
            else if (status == 1){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("该用户未被封禁"));
            }
        }
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("不存在该用户"));
    }

    /**
     * 设置管理员
     * @param id 用户id
     * @return 设置成功与否
     */
    public String updateUserToAdmin(Integer id){
        User user = userMapper.selectUserById(id);
        if (user != null){
            if (user.getStatus() == -1){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("不存在该用户"));
            }
            List<Integer> admins = new ArrayList<>();
            userMapper.selectAdmin().forEach(admin -> admins.add(admin.getId()));
            if (admins.contains(user.getId())){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("该用户已是管理员"));
            }
            else {
                userRoleMapper.updateToAdmin(id);
                return JSON.toJSONString(new Result<>().setCode(1).setMsg("设置管理员成功"));
            }
        }
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("不存在该用户"));
    }

    /**
     * 删除管理员
     * @param id 用户id
     * @return 删除成功与否
     */
    public String updateAdminToUser(Integer id){
        User user = userMapper.selectUserById(id);
        if (user != null){
            if (user.getStatus() == -1){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("不存在该用户"));
            }
            List<Integer> admins = new ArrayList<>();
            userMapper.selectAdmin().forEach(admin -> admins.add(admin.getId()));
            if (admins.contains(user.getId())){
                userRoleMapper.updateToUser(id);
                return JSON.toJSONString(new Result<>().setCode(1).setMsg("删除管理员成功"));
            }
            else {
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("该用户不是管理员"));
            }
        }
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("不存在该用户"));
    }

    /**
     * 查询所有用户
     * @return 所有用户列表
     */
    public String getUsers(){
        List<User> users = userMapper.selectUsers();
        List<Integer> admins = new ArrayList<>();
        userMapper.selectAdmin().forEach(user -> admins.add(user.getId()));
        users.forEach(user -> {
            user.setPassword(null);
            if (admins.contains(user.getId())){
                user.setRole(2);
            }
            else {
                user.setRole(1);
            }
        });
        return JSON.toJSONString(new Result<>().setCode(1).setMsg("查询所有用户成功").setData(users));
    }

    /**
     * 查询管理员用户
     * @return 管理员用户列表
     */
    public String getAdmin(){
        List<User> users = new ArrayList<>();
        userMapper.selectAdmin().forEach(user -> {
            user.setPassword(null);
            user.setRole(2);
            users.add(user);
        });
        return JSON.toJSONString(new Result<>().setCode(1).setMsg("查询管理员用户成功").setData(users));
    }

    /**
     * 获取所有日志
     * @return 日志列表
     */
    public String getLogs(){
        List<Log> logs = logMapper.selectList(null);
        if (logs != null){
            return JSON.toJSONString(new Result<>().setCode(1).setMsg("查询日志成功").setData(logs));
        }
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("无日志信息"));
    }

    /**
     * 分页查询操作日志
     * @param pageNum 第几页
     * @param pageSize 分页大小
     * @return 分页返回操作日志
     */
    public String findPage(Integer pageNum, Integer pageSize)
    {
        PageHelper.startPage(pageNum,pageSize);
        List<Log> operateLogList = logMapper.selectList(null);
        PageInfo<Log> pageInfo = new PageInfo<>(operateLogList);
        return JSON.toJSONString(new Result<>().setCode(1).setMsg("查询成功").setData(pageInfo));
    }

    /**
     * 分页查询操作日志根据日期
     * @param pageNum 页数
     * @param pageSize 分页大小
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    public String getLog(Integer pageNum, Integer pageSize, String startTime, String endTime){
        PageHelper.startPage(pageNum,pageSize);
        List<Log> operateLogList = new ArrayList<>();
        AtomicBoolean success = new AtomicBoolean(true);
        logMapper.selectList(null).forEach(log -> {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try
            {
                if ((df.parse(log.getRequestTime()).after(df.parse(startTime))) && (df.parse(log.getRequestTime()).before(df.parse(endTime)))){
                    operateLogList.add(log);
                }
            } catch (ParseException e)
            {
                success.set(false);
            }
        });
        if (success.get()){
            PageInfo<Log> pageInfo = new PageInfo<>(operateLogList);
            return JSON.toJSONString(new Result<>().setCode(1).setMsg("查询成功").setData(pageInfo));
        }
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("日期类型错误"));
    }

    /**
     * 封ip
     * @param ip ip
     * @return 返回信息
     */
    public String banIp(String ip){
        if (isIp(ip)){
            return JSON.toJSONString(new Result<>().setCode(0).setMsg("请输入合法ip"));
        }
        QueryWrapper<BanIp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ip",ip);
        BanIp banIp = banIpMapper.selectOne(queryWrapper);
        if (banIp == null){
            banIp = new BanIp().setIp(ip);
            banIpMapper.insert(banIp);
            return JSON.toJSONString(new Result<>().setCode(1).setMsg("封禁ip成功"));
        }
        else {
            return JSON.toJSONString(new Result<>().setCode(0).setMsg("该ip已被封禁"));
        }
    }

    /**
     * 解封ip
     * @param ip ip
     * @return 返回信息
     */
    public String unsealIp(String ip){
        if (isIp(ip)){
            return JSON.toJSONString(new Result<>().setCode(0).setMsg("请输入合法ip"));
        }
        QueryWrapper<BanIp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ip",ip);
        BanIp banIp = banIpMapper.selectOne(queryWrapper);
        if (banIp == null){
            return JSON.toJSONString(new Result<>().setCode(0).setMsg("该ip未被封禁"));
        }
        else {
            banIpMapper.delete(queryWrapper);
            return JSON.toJSONString(new Result<>().setCode(1).setMsg("ip解封成功"));
        }
    }

    /**
     * 判断是否是ipv4的ip
     * @param ip ip
     * @return 是否是ipv4
     */
    public boolean isIp(String ip){
        String pattern = "^((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))$";
        return !Pattern.matches(pattern, ip);
    }

    /**
     * 超级管理员发布消息
     * @param msg 消息内容
     * @return 返回信息
     */
    public String publishMessage(String msg){
        Message message = new Message();
        message.setMsg(msg).setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        messageMapper.insert(message);
        return JSON.toJSONString(new Result<>().setCode(1).setMsg("消息发布成功"));
    }

    /**
     * 获取消息
     * @param userId 用户id
     * @return 消息列表
     */
    public String getMessage(String userId){
        List<Message> messageList = messageMapper.selectList(null);
        QueryWrapper<AlRead> alReadQueryWrapper = new QueryWrapper<>();
        alReadQueryWrapper.eq("user_id",userId);
        List<AlRead> alReadList = alReadMapper.selectList(alReadQueryWrapper);
        List<MessageStatus> messageStatusList = new ArrayList<>();
        messageList.forEach(message -> {
            MessageStatus messageStatus = new MessageStatus();
            messageStatus.setMessage(message);
            messageStatus.setStatus(0);
            alReadList.forEach(alRead -> {
                if (alRead.getMsgId().equals(message.getId())){
                    messageStatus.setStatus(1);
                }
            });
            messageStatusList.add(messageStatus);
        });
        return JSON.toJSONString(new Result<>().setCode(1).setMsg("获取消息成功").setData(messageStatusList));
    }

    public String setAlRead(Integer userId,
                            Integer msgId){

        QueryWrapper<AlRead> alReadQueryWrapper = new QueryWrapper<>();
        alReadQueryWrapper.eq("user_id",userId);
        alReadQueryWrapper.eq("msg_id",msgId);
        AlRead alRead1 = alReadMapper.selectOne(alReadQueryWrapper);
        if (alRead1 == null){
            AlRead alRead = new AlRead();
            alRead.setUserId(userId).setMsgId(msgId);
            alReadMapper.insert(alRead);
            return JSON.toJSONString(new Result<>().setCode(1).setMsg("标记已读成功"));
        }
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("本就是已读的消息"));
    }
}
