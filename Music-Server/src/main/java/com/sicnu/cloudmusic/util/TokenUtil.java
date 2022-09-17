package com.sicnu.cloudmusic.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @author 莲童的雨儿
 */
public class TokenUtil
{
    public static String getToken(String userId,String sign){
        return JWT.create().withAudience(userId)
                .withExpiresAt(DateUtil.offsetHour(new Date(),2))
                .sign(Algorithm.HMAC256(sign));
    }
}
