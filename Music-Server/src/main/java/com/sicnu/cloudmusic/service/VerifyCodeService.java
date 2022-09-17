package com.sicnu.cloudmusic.service;

import com.sicnu.cloudmusic.util.VerifyCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author 莲童的雨儿
 */
@Service
public class VerifyCodeService
{
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public VerifyCodeService(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }
    public void getVerificationCode(String time,
                                    HttpServletResponse response) throws IOException
    {
        int width = 200;
        int height = 69;

        BufferedImage verifyImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //生成对应宽高的初始图片
        String randomText = VerifyCodeUtil.drawRandomText(width, height, verifyImg);

        redisTemplate.opsForValue().set(time,randomText);

        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        ImageIO.write(verifyImg, "png", os);
        os.flush();
        os.close();
    }
}
