package com.xiezh.findlost.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiezh.findlost.domain.UserInfo;
import com.xiezh.findlost.service.MyService;
import com.xiezh.findlost.utils.BitmapCache;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.fragmentdemo2.R;

public class UpdateInfoActivity extends AppCompatActivity {

    BitmapCache cache;
    private ImageView new_head;
    private EditText new_username;
    private EditText new_sex;
    private EditText new_name;
    private EditText new_qq;
    private EditText new_phone;
    private UserInfo userinfo;
    private Context context;
    private TextView make_sure;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder service1 = (MyService.MyBinder) service;
            service1.updateInfo(userinfo);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        userinfo = DataManager.userInfo;
        context = this;
        cache = BitmapCache.getCache();
        blidView();


    }

    private void blidView() {
        new_head = (ImageView) findViewById(R.id.new_head_image);
        cache.setHeadBitmapFromCache(userinfo.getHeadImag(), new_head);

        new_username = (EditText) findViewById(R.id.new_username);
        new_username.setText(userinfo.getUserName());

        new_sex = (EditText) findViewById(R.id.new_sex);
        if (userinfo.getSex() == 0) {
            new_sex.setText("男");
        } else {
            new_sex.setText("女");
        }

        new_name = (EditText) findViewById(R.id.new_name);
        new_name.setText(userinfo.getUserRealName());

        Log.i("UpdateActivity", userinfo.getQq() + "");

        new_qq = (EditText) findViewById(R.id.new_qq_a);
        new_qq.setText(userinfo.getQq() + "");

        new_phone = (EditText) findViewById(R.id.new_phone_a);
        new_phone.setText(userinfo.getPhoneNum() + "");

        make_sure = (TextView) findViewById(R.id.make_sure);
        make_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(new_username.getText().toString())) {
                    userinfo.setUserName(new_username.getText().toString());
                }
                if ("男".equals(new_sex.getText().toString())) {
                    userinfo.setSex(0);
                } else if ("女".equals(new_sex.getText().toString())) {
                    userinfo.setSex(1);
                }
                if (!"".equals(new_name.getText().toString())) {
                    userinfo.setUserRealName(new_name.getText().toString());
                }
                if (!"".equals(new_phone.getText().toString())) {
                    userinfo.setPhoneNum(Integer.parseInt(new_phone.getText().toString()));
                }
                if (!"".equals(new_qq.getText().toString())) {
                    userinfo.setQq(Integer.parseInt(new_qq.getText().toString()));
                }


                Intent intent = new Intent(context, MyService.class);
                intent.putExtra("userinfo", userinfo);
                bindService(intent, conn, BIND_AUTO_CREATE);
                finish();
            }
        });

    }

}
