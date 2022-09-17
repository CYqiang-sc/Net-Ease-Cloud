package com.sicnu.cloudmusic.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author 莲童的雨儿
 */
public class IpUtil
{
    public static String getIpAddr(HttpServletRequest request){
        String ip = "";
        String unknown = "unknown";
        String localhostIpv4 = "127.0.0.1";
        String localhostIpv6 = "0:0:0:0:0:0:0:1";
        try
        {
            ip = request.getHeader("X-Forwarded-For");
            if(ip == null || ip.length()==0||unknown.equalsIgnoreCase(ip)){
                ip  =  request.getHeader(" x-forwarded-for ");
            }
            if(ip == null || ip.length()==0||unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("Proxy-Client-IP");
            }
            if(ip == null || ip.length()==0||unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if(ip == null || ip.length()==0||unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if(ip == null || ip.length()==0||unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("HTTP_X_FORWARDED");
            }
            if(ip == null || ip.length()==0||unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
            }
            if(ip == null || ip.length()==0||unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if(ip == null || ip.length()==0||unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("HTTP_FORWARDED_FOR");
            }
            if(ip == null || ip.length()==0||unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("HTTP_FORWARDED");
            }
            if(ip == null || ip.length()==0||unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("HTTP_VIA");
            }
            if(ip == null || ip.length()==0||unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("REMOTE_ADDR");
            }
            if(ip == null || ip.length()==0||unknown.equalsIgnoreCase(ip)){
                ip = request.getHeader("X-Real-IP");
            }
            if(ip == null || ip.length()==0||unknown.equalsIgnoreCase(ip)){
                ip = request.getRemoteAddr();
                if(localhostIpv4.equals(ip)|| localhostIpv6.equals(ip)){
                    InetAddress inet = null;
                    try{
                        inet = InetAddress.getLocalHost();
                    }catch(UnknownHostException e){
                        e.printStackTrace();
                    }
                    assert inet != null;
                    ip = inet.getHostAddress();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (ip == null){
            ip = "-1";
        }
        return ip;
    }
}

