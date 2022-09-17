package com.sicnu.cloudmusic.util;

import com.sicnu.cloudmusic.properties.CloudMusicProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;

/**
 * @author 莲童的雨儿
 */
@Configuration
public class UriUtil
{
    private final List<String> urlList;

    public UriUtil()
    {
        this.urlList = CloudMusicProperties.getUrlList();
    }
    public URI getUri(CloudMusicProperties cloudMusicProperties,
                      String urlName,
                      MultiValueMap<String,String> params)
    {
        URI uri = null;
        for (String u: urlList
             )
        {
            if (u.equals(urlName)){
                String url = getFieldValueByFieldName(u,cloudMusicProperties);
                url = url.split("\\?")[0];
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
                uri = builder.queryParams(params).build().encode().toUri();
                System.out.println(uri);
                break;
            }
        }
        return uri;
    }

    public String getUrl(CloudMusicProperties cloudMusicProperties,
                         String urlName,
                         MultiValueMap<String,String> params)
    {
        return getUri(cloudMusicProperties,urlName,params).toString();
    }
    public String getFieldValueByFieldName(String fieldName,
                                           Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            return  (String)field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
