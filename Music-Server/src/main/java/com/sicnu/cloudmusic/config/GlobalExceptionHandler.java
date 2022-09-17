package com.sicnu.cloudmusic.config;

import com.alibaba.fastjson.JSON;
import com.sicnu.cloudmusic.common.Result;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.ConstraintViolationException;
import java.text.ParseException;

/**
 * @author 莲童的雨儿
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    /**
     * 捕获异常
     * @param e 异常
     * @return 异常信息
     */
    @ExceptionHandler(value = HttpClientErrorException.class)
    public String exceptionHandler(HttpClientErrorException e) {
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("需要登录").setData(e));
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public String exceptionHandler(MissingServletRequestParameterException e){
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("参数缺失").setData(e));
    }

    @ExceptionHandler(value = ParseException.class)
    public String exceptionHandler(ParseException e){
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("日期类型错误").setData(e));
    }
    @ExceptionHandler(value = ConstraintViolationException.class)
    public String exceptionHandler(ConstraintViolationException e){
        return JSON.toJSONString(new Result<>().setCode(0).setMsg("参数错误"));
    }
}
