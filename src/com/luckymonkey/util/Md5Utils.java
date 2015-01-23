package com.luckymonkey.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

/**
 * MD5加密算法
 * 
 * @author xavier
 * @version $Id: Md5Utils.java 2014�?�?�?下午4:01:08 $
 */
public class Md5Utils {

	private static final String TAG = Md5Utils.class.getSimpleName();
    private static final String KEY_MD5 = "MD5";

    /**
     * MD5加密
     * 
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String encryptMD5(byte[] data) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance(KEY_MD5);
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, "md5 加密异常", e);
        }
        md5.update(data);
        byte[] returnbyteArray = md5.digest();
        return byte2hex(returnbyteArray);
    }

    /**
     * MD5加密
     * 
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String encryptMD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance(KEY_MD5);
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, "md5 加密异常", e);
        }
        md5.update(str.getBytes());
        return byte2hex(md5.digest());
    }

    /**
     * MD5加盐加密
     * 
     * @param str
     * @param salt
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String encryptMD5(String str, String salt) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance(KEY_MD5);
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, "md5 加密异常", e);
        }
        md5.update((str + salt).getBytes());
        return byte2hex(md5.digest());
    }

    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }

        return sign.toString();
    }

    public static byte[] hex2byte(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        int len = str.length();
        if (len <= 0 || len % 2 == 1) {
            return null;
        }
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[(i / 2)] = (byte) Integer.decode("0x"
                                                   + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
        }
        return null;
    }
}