package com.xiezh.findlost.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.xiezh.findlost.domain.Item;
import com.xiezh.findlost.fragment.MessageFragment;
import com.xiezh.findlost.fragment.NewListFragment;
import com.xiezh.findlost.fragment.SettingFragment;
import com.xiezh.findlost.service.ItemService;
import com.xiezh.findlost.service.MessageService;
import com.xiezh.findlost.utils.BitmapCache;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.fragmentdemo2.R;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int changeData = 0x111;
    private static final int refreshData = 0x112;
    private static final int ChangeRefreshFlagTrue = 0x113;
    private static final int ChangeRefreshFlagFlase = 0x114;
    private static final int ChangeRefreshTBottom = 0x115;
    private static final int hasNextItem = 0x116;//刷新失物界面
    private static final int hasMessage = 0x117;//刷新消息界面
    private static final int updateSuccess = 0x118;//刷新消息界面
    private static final int updateFalse = 0x119;//刷新消息界面
    private static final int viewOush = 0x120;//刷新消息界面
    private TextView txt_title;
    private FrameLayout fl_content;
    private Context mContext;
    private ArrayList<Item> items = null;
    private FragmentManager fManager = null;
    private long exitTime = 0;
    private TextView menu_home;
    private TextView menu_message;
    private TextView menu_setting;
    private ImageView addImage;
    private boolean refreshFlag = true;
    private NewListFragment nlFragment;
    private MessageFragment msFragment;
    private SettingFragment stFragment;
    private boolean first = true;
    private ImageView search;

    private ItemService.ItemBinder binder;
    private SwipeRefreshLayout refreshLayout;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (ItemService.ItemBinder) service;
            //Toast.makeText(MainActivity.this, "开始下载items", Toast.LENGTH_SHORT).show();
            binder.downloadItem();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private ServiceConnection loadMoreConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (ItemService.ItemBinder) service;
            //Toast.makeText(MainActivity.this, "加载更多items", Toast.LENGTH_SHORT).show();
            binder.downloadItemNext();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case changeData:
                    //收到获取了信息的提示后才模拟点击home键
                    //模拟点击home
                    menu_home.setEnabled(true);
                    menu_message.setEnabled(true);
                    menu_message.performClick();
                    menu_home.performClick();

                    if (refreshLayout.isRefreshing()) {
                        //关闭刷新动画
                        refreshLayout.setRefreshing(false);
                    }
                    unbindService(connection);
                    break;
                case refreshData:
                    menu_home.setEnabled(true);
                    if (true) {
                        if (nlFragment == null) {
                            nlFragment = new NewListFragment(fManager, binder);
                            nlFragment.reFresh();
                        }
                        menu_home.performClick();
                        first = false;
                    }
                    if (refreshLayout.isRefreshing()) {
                        //关闭刷新动画
                        refreshLayout.setRefreshing(false);
                    }
                    unbindService(connection);
                    break;
                case ChangeRefreshFlagTrue:
                    refreshLayout.setEnabled(true);
                    break;
                case ChangeRefreshFlagFlase:
                    refreshLayout.setEnabled(false);
                    break;
                case ChangeRefreshTBottom:
                    //绑定服务
                    Intent intent = new Intent(mContext, ItemService.class);
                    bindService(intent, loadMoreConnection, Service.BIND_AUTO_CREATE);
                    break;
                case hasNextItem:
                    nlFragment.reFresh();
                    unbindService(loadMoreConnection);
                    break;
                case hasMessage:
                    msFragment.add(DataManager.newMessage);
                    Toast.makeText(mContext, DataManager.newMessage.getToUser().getUserName(), Toast.LENGTH_SHORT).show();
                    break;
                case updateSuccess:
                    Toast.makeText(mContext, "更新成功", Toast.LENGTH_LONG).show();
                    break;
                case updateFalse:
                    break;
                case viewOush:
                    Intent intent_viewPush = new Intent(mContext, VIewPushActivity.class);
                    startActivity(intent_viewPush);
                    break;

            }
            ;
        }
    };
    private ServiceConnection messageConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MessageService.MessageBinder binder = (MessageService.MessageBinder) service;
            binder.queryMessage();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        fManager = getFragmentManager();
        DataManager.myHandler = myhandler;

        msFragment = new MessageFragment(mContext);
        stFragment = new SettingFragment(this);

        bindViews();

        //绑定服务
        Intent intent = new Intent(this, ItemService.class);
        bindService(intent, connection, Service.BIND_AUTO_CREATE);

        //绑定服务
        Intent messageIntent = new Intent(this, MessageService.class);
        bindService(messageIntent, messageConnection, Service.BIND_AUTO_CREATE);
    }


    private void bindViews() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        fl_content = (FrameLayout) findViewById(R.id.fl_content);

        menu_home = (TextView) findViewById(R.id.menu_home_txt);
        menu_home.setEnabled(false);
        menu_message = (TextView) findViewById(R.id.menu_message_txt);
        menu_setting = (TextView) findViewById(R.id.menu_setting_txt);
        addImage = (ImageView) findViewById(R.id.add_new_item);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_ly);
        search = (ImageView) findViewById(R.id.search);

        //绑定点击事件
        menu_home.setOnClickListener(this);
        menu_message.setOnClickListener(this);
        menu_setting.setOnClickListener(this);
        search.setOnClickListener(this);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        //给下拉刷新设置监听者
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Intent intent = new Intent(mContext, ItemService.class);
                bindService(intent, connection, Service.BIND_AUTO_CREATE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_home_txt:
                selectAllFalse();
                if (v.isSelected()) {
                } else {
                    refreshLayout.setEnabled(true);
                    txt_title.setText("失物信息");
                    addImage.setImageResource(R.mipmap.add_item);
                    menu_home.setSelected(true);
                    FragmentTransaction ft = fManager.beginTransaction();
                    if (nlFragment == null) {
                        nlFragment = new NewListFragment(fManager, binder);
                    }
                    ft.replace(R.id.fl_content, nlFragment);
                    ft.commit();
                }
                break;
            case R.id.menu_message_txt:
                selectAllFalse();
                if (v.isSelected()) {

                } else {
                    refreshLayout.setEnabled(false);
                    txt_title.setText("消息");
                    addImage.setImageResource(R.mipmap.add_empty);
                    menu_message.setSelected(true);

                    FragmentTransaction ft = fManager.beginTransaction();
                    ft.replace(R.id.fl_content, msFragment);

                    ft.commit();
                }
                break;
            case R.id.menu_setting_txt:
                selectAllFalse();
                if (v.isSelected()) {

                } else {
                    refreshLayout.setEnabled(false);
                    txt_title.setText("设置");
                    addImage.setImageResource(R.mipmap.add_empty);
                    menu_setting.setSelected(true);

                    FragmentTransaction ft = fManager.beginTransaction();
                    ft.replace(R.id.fl_content, stFragment);

                    ft.commit();
                }
                break;
            case R.id.search:
                Intent intent = new Intent(mContext, SearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 把底部txtview设为全未选中状态
     */
    void selectAllFalse() {
        menu_home.setSelected(false);
        menu_message.setSelected(false);
        menu_setting.setSelected(false);
    }

    //点击回退键的处理：判断Fragment栈中是否有Fragment
    //没，双击退出程序，否则像是Toast提示
    //有，popbackstack弹出栈
    @Override
    public void onBackPressed() {
        if (fManager.getBackStackEntryCount() == 0) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        } else {
            fManager.popBackStack();
            txt_title.setText("失物列表");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileOutputStream out;
        BufferedWriter writer = null;
        BitmapCache cache = BitmapCache.getCache();
        try {

            /*out = openFileOutput("iteminfo.in", MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));

            for (Item item :
                    DataManager.datas) {
                writer.write(JSON.toJSONString(item));
                writer.newLine();
            }
            writer.flush();*/
            //保存缓存中的图片
            cache.save(this);


            //保存缓存的messagelist
            out = openFileOutput("messageListCache.in", MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));

            for (com.xiezh.findlost.domain.Message message :
                    DataManager.message) {
                writer.write(JSON.toJSONString(message));
                writer.newLine();
            }
            writer.flush();
            writer.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        DataManager.datas = null;
        cache.clear();
    }
}
