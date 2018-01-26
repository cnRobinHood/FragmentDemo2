package com.xiezh.findlost.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiezh.findlost.domain.Message;
import com.xiezh.findlost.domain.UserInfo;
import com.xiezh.findlost.https.HttpHelper;
import com.xiezh.findlost.utils.DataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiezh on 2017/11/4.
 */

public class MessageService extends Service {

    public static final String TAG = "Messageervice";

    private MessageBinder messageBinder;
    private HttpHelper helper;
    private Handler handler;
    private Bundle bundle;

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new HttpHelper();
        messageBinder = new MessageBinder();
        handler = DataManager.myHandler;
        Log.d(TAG, "onCreate() executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        bundle = intent.getExtras();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messageBinder;
    }

    public class MessageBinder extends Binder {
        public void queryMessage() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    do {
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String s = helper.queryMessage();
                        if ("fail".equals(s)) {

                        } else if ("empty".equals(s)) {

                        } else {

                            JSONObject jsonObject = JSON.parseObject(s);

                            String toId = jsonObject.getString("toId");
                            String fromId = jsonObject.getString("fromId");
                            JSONArray jsonArray = jsonObject.getJSONArray("messageList");
                            String toUser = jsonObject.getString("toUser");
                            int itemId = jsonObject.getIntValue("itemId");


                            UserInfo userinfo = JSON.parseObject(toUser, UserInfo.class);
                            String date = jsonObject.getString("date");

                            List<String> list = new ArrayList<String>();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                list.add((String) jsonArray.get(i));
                            }
                            Log.i(TAG, userinfo.getUserName());

                            Message message = new Message(toId, fromId);
                            message.setDate(date);
                            message.setMessageList(list);
                            message.setType(false);
                            message.setToUser(userinfo);
                            message.setItemId(itemId);

                            Log.i(TAG, message.toString());


                            if (message == null) {
                                Log.i(TAG, "message 为空");
                            } else {
                                Log.i(TAG, message.toString());
                                DataManager.newMessage = message;
                                handler.sendEmptyMessage(0x117);

                                if (message.getFromId().equals(DataManager.currentTalkActivity)) {
                                    DataManager.talkHandler.sendEmptyMessage(0x161);
                                }


                            }
                        }
                    } while (true);
                }
            }).start();

        }

        public void sendMessage(final Message message) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    helper.sendMessage(message);


                    Log.i(TAG, message.toString());
                }
            }).start();
        }
    }
}
