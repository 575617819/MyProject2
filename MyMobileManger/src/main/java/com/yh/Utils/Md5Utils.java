package com.yh.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/3/26.
 */
public class Md5Utils {

    public static String md5Password(String password){

        String result="";
        StringBuffer stringBuffer = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] digest = md.digest(password.getBytes());
            for(byte b:digest){
                int i = b & 0xff;
                String toHexString = Integer.toHexString(i);
                if(toHexString.length()==1){
                    stringBuffer.append("0").append(toHexString);
                }else{
                    stringBuffer.append(toHexString);
                }
            }

            result=stringBuffer.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result;
    }
}
