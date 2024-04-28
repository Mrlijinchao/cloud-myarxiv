package com.lijinchao.utils.mail;

import java.util.Random;

public class Tools {

    public static String randomCode() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    public static String getEmailContent(String code,String emailTitle){
        String Con=null;
        Con="<!DOCTYPE html>\r\n"
                + "<HTML>\r\n"
                + "<HEAD>\r\n"
                + "<META  charset=\" gbk\">\r\n"
                + "<TITLE>"+emailTitle+"</TITLE>\r\n"
                + "</HEAD>\r\n"
                + "<BODY>\r\n"
                + "<h1>你的验证码为："+code
                + " 请妥善保管</h1>\r\n"
                + "    <h1>生活愉快</h1>\r\n"
                + "    <p>每天开心！</p>\r\n"
                + "    <img src=\"https://pic4.zhimg.com/v2-1b1ea64759584f6b9d90f0bfa32cb5af_r.jpg\" width=\"900\" height=\"600\" />\r\n"
                + "</BODY>\r\n"
                + "</HTML>";
        return Con;
    }

}
