package com.xiezh.findlost.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiezh.findlost.adapter.TalkAdapter;
import com.xiezh.findlost.domain.Item;
import com.xiezh.findlost.domain.Message;
import com.xiezh.findlost.domain.UserInfo;
import com.xiezh.findlost.service.MessageService;
import com.xiezh.findlost.service.MyService;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.fragmentdemo2.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TalkActivity extends BaseActivity implements View.OnClickListener {
    private static final int addMessage = 0x161;
    MessageService.MessageBinder myBinder;
    private Item item;
    private File userCache;
    private List<Message> data;
    private TextView back;
    private TextView username;
    private Button send;
    private EditText message;
    private UserInfo userinfo;
    private ListView listView;
    private TalkAdapter adapter;
    private Message needSend;
    private String talkWithName;
    private String talkWithUserId;
    private ServiceConnection connForUserinfo = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder myBinder = (MyService.MyBinder) service;
            myBinder.getUserInfo(item.getCreateByID());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MessageService.MessageBinder) service;
            if (needSend != null) {
                myBinder.sendMessage(needSend);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case addMessage:
                    add(DataManager.newMessage);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        DataManager.talkHandler = handler;

        Intent intent = getIntent();
        data = new ArrayList<>();
        item = (Item) intent.getSerializableExtra("item");

        if (item != null) {
            Log.i("TalkActivity", "传递过来的" + item.toString());
            talkWithName = item.getUserName();
            talkWithUserId = item.getCreateByID();

            Intent intentservice = new Intent(this, MyService.class);
            bindService(intentservice, connForUserinfo, Service.BIND_AUTO_CREATE);

        } else {
            UserInfo userinfo = (UserInfo) intent.getSerializableExtra("userinfo");
            talkWithName = userinfo.getUserName();
            talkWithUserId = userinfo.getUserID();
        }
        blindView();
        init();
        adapter = new TalkAdapter(this, data);
        listView.setAdapter(adapter);


        DataManager.currentTalkActivity = talkWithUserId;
        listView.setSelection(adapter.getCount() - 1);

    }

    private void blindView() {
        back = (TextView) findViewById(R.id.talk_back);
        back.setOnClickListener(this);
        username = (TextView) findViewById(R.id.username);
        username.setText(talkWithName);
        send = (Button) findViewById(R.id.message_send);
        send.setOnClickListener(this);
        message = (EditText) findViewById(R.id.message);
        listView = (ListView) findViewById(R.id.message_list);
    }

    public void init() {

        File cacheDir = getCacheDir();

        userCache = new File(cacheDir, talkWithUserId + ".cache");
        if (!userCache.exists()) {
            try {
                userCache.createNewFile();
            } catch (IOException e) {
                Toast.makeText(this, "创建缓存失败，将不会记录消息", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        FileInputStream in = null;
        BufferedReader bf = null;

        try {
            //读取记录文件
            in = new FileInputStream(userCache);
            bf = new BufferedReader(new InputStreamReader(in));
            String str;

            if (bf != null) {
                while ((str = bf.readLine()) != null && !str.equals("")) {

                    //封装message
                    JSONObject jsonObject = JSON.parseObject(str);

                    String toId = jsonObject.getString("toId");
                    String fromId = jsonObject.getString("fromId");
                    JSONArray jsonArray = jsonObject.getJSONArray("messageList");
                    String toUser = jsonObject.getString("toUser");

                    UserInfo userinfo = JSON.parseObject(toUser, UserInfo.class);
                    String date = jsonObject.getString("date");

                    Boolean type = jsonObject.getBoolean("type");

                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        list.add((String) jsonArray.get(i));
                    }

                    Message message = new Message(toId, fromId);
                    message.setDate(date);
                    message.setMessageList(list);
                    message.setToUser(userinfo);
                    message.setType(type);

                    data.add(message);
                }
                bf.close();
                in.close();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeMessage(Message message) {
        //写入输入输出
        FileOutputStream out = null;
        BufferedWriter bfo = null;
        try {
            out = new FileOutputStream(userCache, true);
            bfo = new BufferedWriter(new OutputStreamWriter(out));

            bfo.write(JSON.toJSONString(message));
            bfo.newLine();
            bfo.flush();

            bfo.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.talk_back:
                finish();
                break;
            case R.id.message_send:
                String txt = message.getText().toString();
                message.setText("");
                if (txt != null & !txt.equals("")) {
                    //组装message
                    /**
                     *
                     private String toId;
                     private String fromId;
                     private List<String> messageList;
                     private UserInfo toUser;
                     private String date;

                     public Message(String toId,String fromId){
                     this.toId = toId;
                     this.fromId = fromId;
                     messageList = new ArrayList<String>();
                     }
                     */
                    //Toast.makeText(this,"得到message",Toast.LENGTH_SHORT).show();
                    userinfo = DataManager.userInfo;
                    //Toast.makeText(this,userinfo.getUserName(),Toast.LENGTH_SHORT).show();
                    if (userinfo != null) {
                        Message message = new Message(talkWithUserId, userinfo.getUserID());
                        //Toast.makeText(this, userinfo.getUserID() + "   to   " +talkWithUserId,Toast.LENGTH_SHORT).show();
                        message.getMessageList().add(txt);
                        //Toast.makeText(this,"userinfo不是空的",Toast.LENGTH_SHORT).show();
                        message.setToUser(userinfo);
                        Date date = new Date();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
                        String format = simpleDateFormat.format(date);
                        message.setDate(format);
                        message.setType(true);
                        message.setItemId(-1);
                        if (adapter != null) {
                            //Toast.makeText(this,"adapter不是空的",Toast.LENGTH_SHORT).show();
                            adapter.add(message);
                            writeMessage(message);
                            needSend = message;
                            if (myBinder == null) {
                                Intent intent = new Intent(this, MessageService.class);
                                bindService(intent, connection, Service.BIND_AUTO_CREATE);
                            } else {
                                myBinder.sendMessage(message);
                            }
                        }
                    }
                }
                break;
        }
    }

    public void add(Message message) {
        adapter.add(message);
        int count = adapter.getCount();
        listView.setSelection(count - 1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
