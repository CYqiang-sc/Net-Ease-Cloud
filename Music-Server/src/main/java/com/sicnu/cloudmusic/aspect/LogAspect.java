package com.sicnu.cloudmusic.aspect;

import com.sicnu.cloudmusic.annotation.Log;
import com.sicnu.cloudmusic.mapper.LogMapper;
import com.sicnu.cloudmusic.util.HttpContextUtil;
import com.sicnu.cloudmusic.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author 莲童的雨儿
 */
@Aspect
@Component
@Slf4j
public class LogAspect
{
    private final LogMapper logMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public LogAspect(LogMapper logMapper){
        this.logMapper = logMapper;
    }

    @Pointcut("@annotation(com.sicnu.cloudmusic.annotation.Log)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable{
        Object result = point.proceed();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String name = method.getName();
        com.sicnu.cloudmusic.entity.Log operationLog = new com.sicnu.cloudmusic.entity.Log();
        Log myLog = method.getAnnotation(Log.class);
        //请求的方法名
        log.info(name);
        operationLog.setMethod(name);
        //操作描述
        log.info(myLog.value());
        operationLog.setOperation(myLog.value());
        Object[] args = point.getArgs();
        //参数
        boolean encode = myLog.encode();
        if (encode)
        {
            //密码加密
            args[1] = passwordEncoder.encode((CharSequence) args[1]);
        }
        log.info(Arrays.toString(args));
        operationLog.setArgs(Arrays.toString(args));
        //请求
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        //ip
        String ip = IpUtil.getIpAddr(request);
        log.info(ip);
        operationLog.setIp(ip);
        operationLog.setRequestTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        logMapper.insert(operationLog);
        return result;
    }

}
