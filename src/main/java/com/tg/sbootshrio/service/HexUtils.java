package com.tg.sbootshrio.service;

/**
 * @program: chargingpile
 * @description: 转成16进制卡号的工具类
 * @author: Mr.ch
 * @create: 2020-08-31
 **/
public class HexUtils {

    /**
     * 解密后的卡号需要高低位转换(不足8位前面补0)
     * 才能得到需要的16进制卡号
     * @param serialNum
     * @return
     */
    public static String getHexCardNum(int serialNum) {
        return reverse(hex(Integer.parseInt(serialNum+"")));
    }

    /**
     * 10进制转16进制
     * @param serialNum
     * @return
     */
    public static String hex(int serialNum) {
        return  String.format("%08x", Integer.valueOf(serialNum));
    }

    /**
     * 十六进制字符串高低位转换
     * @param hex
     * @return
     */
    public static String reverse( String hex) {
        final char[] charArray = hex.toCharArray();
        final int length = charArray.length;
        final int times = length / 2;
        for (int c1i = 0; c1i < times; c1i += 2) {
            final int c2i = c1i + 1;
            final char c1 = charArray[c1i];
            final char c2 = charArray[c2i];
            final int c3i = length - c1i - 2;
            final int c4i = length - c1i - 1;
            charArray[c1i] = charArray[c3i];
            charArray[c2i] = charArray[c4i];
            charArray[c3i] = c1;
            charArray[c4i] = c2;
        }
        return new String(charArray).toUpperCase();
    }
}

