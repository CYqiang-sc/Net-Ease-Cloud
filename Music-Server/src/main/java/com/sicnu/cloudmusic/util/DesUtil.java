package com.sicnu.cloudmusic.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * @author 莲童的雨儿
 */
public class DesUtil
{

    private final String sKey;

    public DesUtil() {
        //默认构造函数提供默认密钥
        sKey = "fmyniubi";
    }

    private Cipher makeCipher() throws Exception{
        return Cipher.getInstance("DES");
    }

    private SecretKey makeKeyFactory() throws Exception{
        SecretKeyFactory des = SecretKeyFactory.getInstance("DES");
        return des.generateSecret(new DESKeySpec(sKey.getBytes()));
    }

    public String encrypt(String text) throws Exception{
        Cipher cipher = makeCipher();
        SecretKey secretKey = makeKeyFactory();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return new String(Base64.encodeBase64(cipher.doFinal(text.getBytes())));
    }

    public String decrypt(String text) throws Exception{
        Cipher cipher = makeCipher();
        SecretKey secretKey = makeKeyFactory();
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.decodeBase64(text.getBytes())));
    }

}
