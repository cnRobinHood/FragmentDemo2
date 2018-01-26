package com.xiezh.findlost.utils;

import android.graphics.Bitmap;
import android.os.Handler;

import com.xiezh.findlost.adapter.CameraImageAdapter;
import com.xiezh.findlost.domain.Item;
import com.xiezh.findlost.domain.Message;
import com.xiezh.findlost.domain.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiezh on 2017/11/3.
 */

public class DataManager {
    public static UserInfo userInfo;
    public static List<Item> push = new ArrayList<>();//查看自己发布的失物时查看的
    public static UserInfo talkuserInfo;
    public static Map<String, Bitmap> imageList = new HashMap();
    public static Handler myHandler;
    public static String remarkStr;//新item的描述
    public static List<String> newItemImage = new ArrayList<>();//新的item的照片
    public static CameraImageAdapter cameraImageAdapter;//添加新itemactivity中的照片adapter;
    public static int currentPosition;//viewImageActivity中当前图片的位置
    public static List<Item> datas;//主界面listview中的数据
    public static Message newMessage;//主界面listview中需要添加的数据
    public static List<Message> message = new ArrayList<>();//主界面messageview中需要添加的数据
    public static String currentTalkActivity;
    public static Handler talkHandler;//talkactivity中的handler
    private static String username;
    private static String password;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        DataManager.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        DataManager.password = password;
    }

    public static Item getItemById(int id) {

        for (Item item : datas) {
            if (item.getItemID() == id) {
                return item;
            }
        }
        return null;
    }

    public static void clear() {
        push = new ArrayList<>();
        imageList = new HashMap();
        message = new ArrayList<>();
    }
}
