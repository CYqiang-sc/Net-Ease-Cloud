package com.sicnu.cloudmusic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author 莲童的雨儿
 */
@SpringBootApplication
@MapperScan("com.sicnu.cloudmusic.mapper")
public class CloudMusicApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(CloudMusicApplication.class, args);
    }
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }

}
