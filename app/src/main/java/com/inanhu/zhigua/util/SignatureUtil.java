package com.inanhu.zhigua.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SignatureUtil {

    /**
     * 验证
     *
     * @param token
     * @return true 验证通过，false 验证失败
     */
    public static boolean check(String signature, String token,
                                String timestamp, String nonce) throws NoSuchAlgorithmException {
        String sha1 = encode(token, timestamp, nonce);
        return sha1.equals(signature);
    }

    /**
     * 得到加密后的数据
     *
     * @return
     */
    public static String encode(String token, String timestamp, String nonce) throws NoSuchAlgorithmException {
        String[] sa = {token, timestamp, nonce};
        Arrays.sort(sa);
        String sortStr = sa[0] + sa[1] + sa[2];
        return sha1Hex(sortStr);
    }

    public static String sha1Hex(String info) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA1");
        byte[] srcBytes = info.getBytes();
        // 使用srcBytes更新摘要
        sha.update(srcBytes);
        // 完成哈希计算，得到result
        byte[] resultBytes = sha.digest();
        return hexString(resultBytes);
    }

    public static String hexString(byte[] bytes) {
        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            int val = ((int) bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
