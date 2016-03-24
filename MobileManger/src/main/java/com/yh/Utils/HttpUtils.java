package com.yh.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/3/24.
 */
public class HttpUtils {

    public static String getTextFromStream(InputStream inputStream){

        String result="";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        byte[] bys=new byte[1024];
        int len=0;

        try {
            while((len=inputStream.read(bys,0,1024))!=-1){
                byteArrayOutputStream.write(bys,0,len);
            }
            
            byteArrayOutputStream.close();
            result = byteArrayOutputStream.toString("gbk");


        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
    
}
