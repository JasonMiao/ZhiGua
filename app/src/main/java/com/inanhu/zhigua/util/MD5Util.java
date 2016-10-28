package com.inanhu.zhigua.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Util {
    public static String encrypt(String str) {
        String md5Str = null;
        //获得一个摘要加密的工具对象，该对象
        //使用md5算法来生成一个摘要。
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //生成摘要
        byte[] buf = md.digest(str.getBytes());
        //将摘要转变成一个字符串
        //经常使用BASE64Encoder将一个字节数组
        //转换成一个字符串。
        Base64Encoder encoder = new Base64Encoder();
        md5Str = encoder.encode(buf);
        return md5Str;
    }
}
