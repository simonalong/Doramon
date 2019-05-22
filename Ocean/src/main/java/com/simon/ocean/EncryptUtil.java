package com.simon.ocean;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.experimental.UtilityClass;

/**
 * @author zhouzhenyong
 * @since 2019/2/22 上午11:20
 */
@UtilityClass
public class EncryptUtil {

    /**
     * 传入文本内容，返回 encrypt-256 串
     */
    public String SHA256(final String strText) {
        return encrypt(strText, "SHA-256");
    }

    /**
     * 传入文本内容，返回 encrypt-512 串
     */
    public String SHA512(final String strText) {
        return encrypt(strText, "SHA-512");
    }

    /**
     * 传入文本内容，返回 MD5 串
     */
    public String MD5(final String strText){
        return encrypt(strText, "MD5");
    }

    /**
     * 字符串 encrypt 加密
     */
    private String encrypt(final String str, final String strType) {
        MessageDigest messageDigest;
        String encodeStr = "";
        if (null == str || str.length() == 0) {
            return encodeStr;
        }
        try {
            messageDigest = MessageDigest.getInstance(strType);
            messageDigest.update(str.getBytes());

            // 将byte 转换为字符展示出来
            StringBuilder stringBuffer = new StringBuilder();
            String temp;
            for (byte aByte : messageDigest.digest()) {
                temp = Integer.toHexString(aByte & 0xFF);
                if (temp.length() == 1) {
                    //1得到一位的进行补0操作
                    stringBuffer.append("0");
                }
                stringBuffer.append(temp);
            }
            encodeStr = stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }
}
