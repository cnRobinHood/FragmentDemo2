package com.xiezh.findlost.activity;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.xiezh.findlost.domain.Item;
import com.xiezh.findlost.service.MyService;
import com.xiezh.findlost.utils.BitmapCache;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.fragmentdemo2.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BeforeLoginActivity extends AppCompatActivity {
    private final Handler myHandler = new Handler() {
        @Override
        //重写handleMessage方法,根据msg中what的值判断是否执行后续操作
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    Intent intent = new Intent(BeforeLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    //unbindService(connection);
                    finish();
                    break;
                case 0x124:
                    Intent intent1 = new Intent(BeforeLoginActivity.this, LoginActivity.class);
                    startActivity(intent1);
                    //unbindService(connection);
                    finish();
                    break;
            }
        }
    };
    private String TAG = "BeforeLoginActivity";
    private String username;
    private String password;
    private Intent intent;
    private Activity context;
    private MyService.MyBinder myBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyService.MyBinder) service;
            myBinder.startLogin();
            Log.i(TAG, "startService");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_login);
        context = this;
        DataManager.myHandler = myHandler;

        //Toast.makeText(this,"checkUserInfo",Toast.LENGTH_SHORT).show();

        //deleteFile("userInfo.in.in");
        checkUserInfo();
       /* String[] strings = fileList();
        Toast.makeText(this,strings[0],Toast.LENGTH_SHORT).show();*/


        delayTime();
    }

    public void checkUserInfo() {
        FileInputStream in = null;
        try {

            in = this.openFileInput("userInfo.in");
            //Toast.makeText(this, "读取userinfo.in", Toast.LENGTH_SHORT).show();

            if (in != null) {
                //Toast.makeText(this,"存在userinfo.in",Toast.LENGTH_SHORT).show();
                //Toast.makeText(this,"存在配置文件",Toast.LENGTH_SHORT).show();
                StringBuilder sb = new StringBuilder();
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = in.read(bytes)) != -1) {
                    sb.append(new String(bytes));
                }
                String[] split = sb.toString().split(";");

                username = split[0];
                password = split[1];

                DataManager.setUsername(username);
                DataManager.setPassword(password);
            } else {
                //Toast.makeText(this,"无userinfo.in，打开loginActivity",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }

            //读取dataManager中的信息 data 主界面需要加载的信息
            in = this.openFileInput("iteminfo.in");
            if (in != null) {
                DataManager.datas = new ArrayList<>();
                BufferedReader bd = new BufferedReader(new InputStreamReader(in)); //
                String str = null;
                int i = 0;
                while ((str = bd.readLine()) != null && !str.equals("")) {
                    Item item = JSON.parseObject(str, Item.class);
                    if (item != null) {
                        DataManager.datas.add(item);
                    }
                }
            }

            //读取缓存的bitmap
            in = this.openFileInput("bitmapList.in");
            if (in != null) {
                BufferedReader bd = new BufferedReader(new InputStreamReader(in)); //
                String str = null;
                int i = 0;
                while ((str = bd.readLine()) != null && !str.equals("")) {
                    BitmapCache.nameList.add(str);
                }
                File cacheFile = this.getCacheDir();
                File file = null;
                FileInputStream bitmapIn = null;
                BitmapCache cache = BitmapCache.getCache();
                for (String name : BitmapCache.nameList) {
                    file = new File(cacheFile, name);
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    if (bitmap != null)
                        BitmapCache.addBitMap(name, bitmap);
                    //Toast.makeText(this, "图片缓存存在", Toast.LENGTH_SHORT).show();
                    file.delete();
                }
            }

            in = this.openFileInput("messageListCache.in");
            if (in != null) {
                BufferedReader bd = new BufferedReader(new InputStreamReader(in)); //
                String str = null;
                int i = 0;
                while ((str = bd.readLine()) != null && !str.equals("")) {
                    com.xiezh.findlost.domain.Message message = JSON.parseObject(str, com.xiezh.findlost.domain.Message.class);
                    if (message != null) {
                        DataManager.message.add(message);
                    }
                }
            }
        } catch (Exception e) {
            //Toast.makeText(this, "文件损坏读取错误 异常", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void startService() {
        intent = new Intent(BeforeLoginActivity.this, MyService.class);
        //Toast.makeText(this,"开始service",Toast.LENGTH_SHORT).show();
        bindService(intent, connection, Service.BIND_AUTO_CREATE);
    }

    public void delayTime() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startService();
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 2000);

    }
}
