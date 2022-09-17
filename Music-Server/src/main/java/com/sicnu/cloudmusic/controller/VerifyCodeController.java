package com.sicnu.cloudmusic.controller;

import com.sicnu.cloudmusic.service.VerifyCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;

/**
 * @author 莲童的雨儿
 */
@Api(value = "VerifyCodeController",tags = {"验证码"})
@RestController
@Validated
public class VerifyCodeController
{
    private final VerifyCodeService verifyCodeService;

    @Autowired
    public VerifyCodeController(VerifyCodeService verifyCodeService){
        this.verifyCodeService = verifyCodeService;
    }
    @RequestMapping(value = "/verify/code",method = RequestMethod.GET)
    @ApiOperation("获取验证码图片")
    public void getVerificationCode(@RequestParam(value = "time") @NotBlank String time,
                                    HttpServletResponse response) throws IOException
    {
        verifyCodeService.getVerificationCode(time,response);
    }
}
