package com.sicnu.cloudmusic.runner;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Set;

/**
 * @author 莲童的雨儿
 */
@Component
@Slf4j
public class MyApplicationRunner implements ApplicationRunner
{
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public MyApplicationRunner(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void run(ApplicationArguments args)
    {
        delAll();
        log.info("redis缓存删除完成");
    }
    /**
     * 删除所有缓存
     */
    public void delAll() {
        Set<String> keys = stringRedisTemplate.keys("*");
        if (ObjectUtils.isNotEmpty(keys)) {
            stringRedisTemplate.delete(keys);
        }
    }
}
