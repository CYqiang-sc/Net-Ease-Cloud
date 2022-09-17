package com.sicnu.cloudmusic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger配置 去掉了@Configuration注解取消swagger
 * @author 莲童的雨儿
 */
@EnableSwagger2
public class SwaggerConfig
{
    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // Swagger会扫描该包下所有Controller定义的API，并产生文档内容（除了被@ApiIgnore注解的API）
                .apis(RequestHandlerSelectors.basePackage("com.sicnu.cloudmusic.controller"))
                // 可以根据url路径设置哪些请求加入文档，忽略哪些请求
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                // 页面标题
                .title("网易云api")
                // 描述
                .description("网易云管理系统API文档")
                .build();
    }
}
