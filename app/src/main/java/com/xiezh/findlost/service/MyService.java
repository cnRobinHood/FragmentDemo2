package com.xiezh.findlost.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.xiezh.findlost.domain.UserInfo;
import com.xiezh.findlost.https.HttpHelper;
import com.xiezh.findlost.utils.DataManager;

import java.io.FileOutputStream;

/**
 * Created by xiezh on 2017/11/4.
 */

public class MyService extends Service {

    public static final String TAG = "MyService";

    private MyBinder mBinder = new MyBinder();
    private HttpHelper helper;
    private Handler handler = DataManager.myHandler;
    private Bundle bundle;
    private Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new HttpHelper();
        Log.d(TAG, "onCreate() executed");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        bundle = intent.getExtras();
        this.intent = intent;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public void startLogin() {
            Log.w(TAG, "startLogin executed");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    try {
                        String username = DataManager.getUsername();
                        String password = DataManager.getPassword();
                        if (username == null | "".equals(username)) {
                            Log.i(TAG, "username为空");
                            handler.sendEmptyMessage(0x124);

                        } else if (password == null | "".equals(password)) {
                            Log.i(TAG, "username为空");
                            handler.sendEmptyMessage(0x124);
                        } else {
                            String str = helper.login(username, password);
                            Log.i(TAG, str + "aaaa");
                            UserInfo userInfo = JSON.parseObject(str, UserInfo.class);
                            String userinfoJSON = JSON.toJSONString(userInfo);

                            FileOutputStream out = openFileOutput("userInfo.in", MODE_PRIVATE);

                            //拼接写入的数据
                            String data = username + ";" + password + ";" + userinfoJSON;
                            out.write(data.getBytes());

                            out.close();

                            Log.i("UserInfo JSON", userinfoJSON);
                            Log.i("UserInfo usernmae", username);
                            Log.i("UserInfo password", password);

                            if (userInfo.getState() != -1 && userInfo.getState() != 0) {
                                DataManager.userInfo = userInfo;
                                handler.sendEmptyMessage(0x123);
                                Log.i(TAG, "0x123");
                                byte[] imagByte = new HttpHelper().getImagByte(userInfo.getHeadImag());
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imagByte, 0, imagByte.length);
                                DataManager.imageList.put(userInfo.getHeadImag(), bitmap);
                            } else {
                                handler.sendEmptyMessage(0x124);
                                Log.i(TAG, "0x124");
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Looper.loop();
                }
            }).start();
        }

        public void getUserInfo(final String id) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String s = helper.queryUserinfo(id);
                        UserInfo userInfo = JSON.parseObject(s, UserInfo.class);

                        if (!"-1".equals(userInfo.getId())) {
                            DataManager.talkuserInfo = userInfo;
                            Log.i(TAG, "查找到的userinfo " + userInfo.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        public void updateInfo(final UserInfo userInfo) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        if (helper.updateUserinfo(userInfo)) {
                            handler.sendEmptyMessage(0x118);
                        } else {
                            handler.sendEmptyMessage(0x119);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }
}
