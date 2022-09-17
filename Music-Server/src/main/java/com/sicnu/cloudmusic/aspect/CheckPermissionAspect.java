package com.sicnu.cloudmusic.aspect;

import com.alibaba.fastjson.JSON;
import com.sicnu.cloudmusic.common.Result;
import com.sicnu.cloudmusic.util.HttpContextUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author 莲童的雨儿
 */
@Aspect
@Component
public class CheckPermissionAspect
{

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public CheckPermissionAspect(RedisTemplate<String,String> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Pointcut("@annotation(com.sicnu.cloudmusic.annotation.CheckPermission)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable{
        Object result;
        try
        {
            HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
            String token = request.getHeader("token");
            if (token == null){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("缺少用户令牌"));
            }
            String permissions = Objects.requireNonNull(redisTemplate.opsForValue().get(token)).replace("[","").replace("]","").replace(" ","");
            List<String> permissionList = Arrays.asList(permissions.split(","));
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            String name = method.getName();
            if (!permissionList.contains(name)){
                return JSON.toJSONString(new Result<>().setCode(0).setMsg("无权限"));
            }
            result = point.proceed();
        }catch (NullPointerException ignore){
            return JSON.toJSONString(new Result<>().setCode(0).setMsg("无权限"));
        }
        return result;
    }
}
