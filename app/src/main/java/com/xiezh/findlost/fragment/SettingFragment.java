package com.xiezh.findlost.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiezh.findlost.activity.LoginActivity;
import com.xiezh.findlost.activity.UpdateInfoActivity;
import com.xiezh.findlost.domain.UserInfo;
import com.xiezh.findlost.service.ItemService;
import com.xiezh.findlost.utils.BitmapCache;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.fragmentdemo2.R;

import java.io.FileOutputStream;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by xiezh on 2017/10/29.
 */

public class SettingFragment extends Fragment implements View.OnClickListener {
    private LinearLayout userinfo;
    private ImageView head_image;
    private TextView username;
    private TextView userid;
    private TextView st;
    private TextView clear;
    private TextView pushitem;
    private TextView changeinfo;
    private BitmapCache cache;
    private TextView size;
    private UserInfo userInfo;
    private Activity activity;
    private ItemService.ItemBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (ItemService.ItemBinder) service;
            binder.downloadItemById(userInfo.getUserID());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public SettingFragment() {

    }


    public SettingFragment(Activity activity) {
        cache = BitmapCache.getCache();
        userInfo = DataManager.userInfo;
        this.activity = activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_setting, container, false);
        blindView(view);
        return view;
    }

    private void blindView(View view) {
        head_image = view.findViewById(R.id.head_Image);
        username = view.findViewById(R.id.username);
        userid = view.findViewById(R.id.userid);
        //设置头像
        if (cache != null) {
            cache.setHeadBitmapFromCache(userInfo.getHeadImag(), head_image);
        }
        username.setText(userInfo.getUserName());
        userid.setText("id:" + userInfo.getUserID());

        //绑定监视器
        userinfo = view.findViewById(R.id.userinfo);
        userinfo.setOnClickListener(this);

        st = view.findViewById(R.id.st);
        st.setOnClickListener(this);

        clear = view.findViewById(R.id.clear);
        clear.setOnClickListener(this);

        pushitem = view.findViewById(R.id.pushitem);
        pushitem.setOnClickListener(this);

        changeinfo = view.findViewById(R.id.changeinfo);
        changeinfo.setOnClickListener(this);

        size = view.findViewById(R.id.size);
        if ((BitmapCache.getSize() + "").length() >= 5) {
            size.setText((BitmapCache.getSize() + "").substring(0, 4) + "MB");
        } else {
            size.setText((BitmapCache.getSize() + "") + "MB");
        }
        size.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userinfo:
                break;
            case R.id.st:
                //做的退出登陆
                try {
                    FileOutputStream out = activity.openFileOutput("userInfo.in", MODE_PRIVATE);

                    //拼接写入的数据
                    String data = DataManager.getUsername() + ";asdasdvc" + DataManager.getPassword().concat("asdasdasdas") + ";" + "      ";
                    out.write(data.getBytes());

                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent1 = new Intent(activity, LoginActivity.class);
                startActivity(intent1);
                activity.finish();

                break;
            case R.id.size:
            case R.id.clear:
                DataManager.clear();
                BitmapCache.clear();
                if ((BitmapCache.getSize() + "").length() > 5) {
                    size.setText((BitmapCache.getSize() + "").substring(0, 5) + "MB");
                } else {
                    size.setText(BitmapCache.getSize() + "MB");
                }
                Toast.makeText(getActivity(), "数据清除成功", Toast.LENGTH_LONG).show();
                break;
            case R.id.pushitem:
                if (binder == null) {
                    Intent intent_push = new Intent(getActivity(), ItemService.class);
                    getActivity().bindService(intent_push, connection, BIND_AUTO_CREATE);
                } else {
                    binder.downloadItemById(userInfo.getUserID());
                }
                break;
            case R.id.changeinfo:
                Intent intent = new Intent(getActivity(), UpdateInfoActivity.class);
                startActivity(intent);
                break;

        }
    }
}
