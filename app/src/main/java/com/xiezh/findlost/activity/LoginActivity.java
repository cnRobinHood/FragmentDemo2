package com.xiezh.findlost.activity;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiezh.findlost.https.HttpHelper;
import com.xiezh.findlost.service.MyService;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.fragmentdemo2.R;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    final HttpHelper helper = new HttpHelper();
    String str = null;
    private TextView usernameTextView;
    private TextView passwordTextView;
    private ImageView loginHead;
    private Button loginButton;
    private TextView registerButton;
    private TextView forgetButton;
    private String username;
    private String password;
    private Bundle bundle;
    private Intent intent;
    private MyService.MyBinder myBinder;
    private Activity activity;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyService.MyBinder) service;
            //Toast.makeText(LoginActivity.this, "service开始", Toast.LENGTH_SHORT).show();
            myBinder.startLogin();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    final Handler myHandler = new Handler() {
        @Override
        //重写handleMessage方法,根据msg中what的值判断是否执行后续操作
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                    activity.finish();
                    break;
                case 0x124:
                    Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                    try {
                        unbindService(connection);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DataManager.myHandler = myHandler;

        bindView();
    }

    void bindView() {
        activity = this;
        usernameTextView = (TextView) findViewById(R.id.username_text);
        passwordTextView = (TextView) findViewById(R.id.passwrod_text);
        loginButton = (Button) findViewById(R.id.login_botton);
        registerButton = (TextView) findViewById(R.id.register);
        forgetButton = (TextView) findViewById(R.id.forget);


        String username_text = DataManager.getUsername();
        if (username_text != null) {
            usernameTextView.setText(username_text);
        }
        //绑定登陆按钮登陆事件和注册事件

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_botton:
                username = usernameTextView.getText().toString();
                password = passwordTextView.getText().toString();
                startService();
                break;
            case R.id.register:
                Toast.makeText(this, "暂不提供找回密码服务", Toast.LENGTH_LONG).show();
                break;
        }




       /* new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    str = helper.login(username, password);
                    UserInfo userInfo = JSON.parseObject(str, UserInfo.class);
                       if(userInfo.getState() != 0) {
                           myHandler.sendEmptyMessage(0x123);
                           byte[] imagByte = new HttpHelper().getImagByte(userInfo.getHeadImag());
                           Bitmap bitmap = BitmapFactory.decodeByteArray(imagByte, 0, imagByte.length);

                           DataManager.userInfo = userInfo;
                           DataManager.imageList.put(userInfo.getHeadImag(), bitmap);
                       }else{
                           myHandler.sendEmptyMessage(0x124);
                       }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/

    }

    public void startService() {
        DataManager.setUsername(username);
        DataManager.setPassword(password);

        intent = new Intent(LoginActivity.this, MyService.class);
        bindService(intent, connection, Service.BIND_AUTO_CREATE);
    }

}
