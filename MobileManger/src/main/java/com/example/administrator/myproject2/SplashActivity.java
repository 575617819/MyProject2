package com.example.administrator.myproject2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yh.Utils.HttpUtils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {


    private String TAG="SplashActivity";
    private int MESSAGE_OK=1;

    private String versionName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //获取当前应用版本号
        versionName=getVersionName();
        Log.i(TAG, "versionName:" + versionName);


        //获取服务器端应用版本号
        getNewVersionName();


    }

    public String getVersionName(){

        String versionName="";

        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;

    }


    Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 1:
                    String[] obj = (String[])msg.obj;
                    String newVersion = obj[0];

                    Log.i(TAG,"handler");
                    Log.i(TAG,newVersion);
                    Log.i(TAG,versionName);

                    //防止服务器有多个版本
                    float newVersionFloat = Float.parseFloat(newVersion);
                    float versionNameFlat = Float.parseFloat(versionName);

                    if(newVersionFloat>versionNameFlat){

                        update(obj);


                    }else{
                        //进入HomeActivity页面
                        intoHomeActivity();
                    }


                    break;
            }
        }
    };


    public void update(final String[] obj){

        String[] items={"版本号：2.0","更新时间：2016.03.31","  ","新版特性：","全新改版的UI,更清爽，更好用","强大的优化功能，更精确，更懂你"};
        new AlertDialog.Builder(SplashActivity.this)
                .setTitle("新版本已准备好")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("立即升级", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String downloadUrl = obj[1];
                        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                        asyncHttpClient.get("http://192.168.3.21/MobileManager"+downloadUrl,new MyAsyncHttpHandler());


                    }
                })
                .setNegativeButton("稍后安装",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //进入HomeActivity页面
                        intoHomeActivity();

                    }
                }).show();

    }


    //内部类
    class MyAsyncHttpHandler extends AsyncHttpResponseHandler{

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            super.onSuccess(statusCode, headers, responseBody);

            //调用安装应用的程序安装下载好的新版本应用
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MobileManager.apk");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(responseBody);
                fileOutputStream.close();

                install(file);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            super.onFailure(statusCode, headers, responseBody, error);

            Toast.makeText(SplashActivity.this,"网络异常，下载失败",Toast.LENGTH_SHORT).show();

            //跳转到主页面
            intoHomeActivity();

        }
    }


    public void install(File file){

        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        startActivity(intent);

    }




    public void getNewVersionName(){

        new Thread(){
            @Override
            public void run() {
                super.run();

                String path="http://192.168.3.21/MobileManager/newVersionInformation.json";
                try {

                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);

                    if(connection.getResponseCode()==200){

                        InputStream inputStream = connection.getInputStream();

                        String textFromStream = HttpUtils.getTextFromStream(inputStream);

                        JSONObject jsonObject = new JSONObject(textFromStream);
                        String newVersion = jsonObject.getString("version");
                        String downloadUrl = jsonObject.getString("URL");

                        String[] newVersionInformation={newVersion,downloadUrl};

                        Message message = new Message();
                        message.what=MESSAGE_OK;
                        message.obj=newVersionInformation;
                        handler.sendMessage(message);

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }.start();
    }


    //进入主页面的方法
    public void intoHomeActivity(){

        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
