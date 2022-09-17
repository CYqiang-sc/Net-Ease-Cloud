package com.sicnu.cloudmusic.util;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * @author 莲童的雨儿
 */
public class EncyUtil
{

    private void ency(String url, String username, String password, String salt){
        BasicTextEncryptor encrypt = new BasicTextEncryptor();
        //加密所需的salt(盐)
        encrypt.setPassword(salt);
        String username1 = encrypt.encrypt(username);
        String password1 = encrypt.encrypt(password);
        String url1 = encrypt.encrypt(url);
        System.out.println("url:" + url1);
        System.out.println("username:" + username1);
        System.out.println("password:" + password1);
    }
}
