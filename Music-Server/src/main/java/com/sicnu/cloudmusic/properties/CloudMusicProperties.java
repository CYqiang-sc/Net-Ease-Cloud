package com.sicnu.cloudmusic.properties;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 莲童的雨儿
 */
@Configuration
@Data
public class CloudMusicProperties
{
    /**
     * IP地址
     */
    private final String ip = "http://127.0.0.1:3000";
    /**
     * 登录
     */
    private final String loginUrl = ip + "/login/cellphone?phone=xxx&password=yyy";


    private final String existenceUrl = ip + "/cellphone/existence/check?phone=13xxx";
    /**
     * 获取链接地址列表
     * @return 链接地址列表
     */
    public static List<String> getUrlList(){
        Class<CloudMusicProperties> clazz = CloudMusicProperties.class;
        Field[] fields = clazz.getDeclaredFields();
        List<String> urlList = new ArrayList<>();
        for (Field f:fields
        )
        {
            urlList.add(f.getName());
        }
        return urlList;
    }

}
