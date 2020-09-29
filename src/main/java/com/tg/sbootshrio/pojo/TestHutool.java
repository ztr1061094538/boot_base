package com.tg.sbootshrio.pojo;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;

import java.text.DecimalFormat;

/**
 * Created by DHAdmin on 2020/8/4.
 */
public class TestHutool {

    public static void main(String[] args) {
        //RandomUtil
//        int a=1;
//        String s = Convert.toStr(a);
//        System.out.println(s);
//        String str = "123456";
//        String md5Str = SecureUtil.md5(str);
//        System.out.println("md5Str = " + md5Str);
        DecimalFormat df=new DecimalFormat("0.00");
        int a=1;
        int b=14;
        String c=a / (b*100) +"";
        String format = df.format((float) a*100/ b)+"%";
        System.out.println("c = " + c);
        System.out.println("format = " + format);


    }
}
