package com.xiezh.findlost.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.xiezh.findlost.domain.Item;
import com.xiezh.findlost.domain.UserInfo;
import com.xiezh.findlost.https.HttpHelper;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.findlost.utils.FastJsonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiezh on 2017/11/4.
 */

public class ItemService extends Service {

    public static final String TAG = "Itemervice";

    private ItemBinder itemBinder;
    private HttpHelper helper;
    private Handler handler;
    private Bundle bundle;

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new HttpHelper();
        itemBinder = new ItemBinder();
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
        return itemBinder;
    }

    public class ItemBinder extends Binder {

        /**
         * 上传item
         */
        public void uploadItem(final Context context) {
            new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();
                    String imageIds = "";
                    //拼接item
                    UserInfo userinfo = DataManager.userInfo;
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateString = formatter.format(date);

                    Item item = new Item(userinfo.getUserID(), userinfo.getHeadImag(), userinfo.getUserName(), DataManager.remarkStr, null, dateString);

                    DataManager.remarkStr = "";
                    Map<String, Object> map = new HashMap<>();

                    for (String filePath : DataManager.newItemImage) {
                        File file = new File(filePath);
                        String name = file.getName();
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                        int KB = bitmap.getByteCount() / 1024;//bitmap的大小，压缩到200kb左右
                        if (KB > 200) {
                            try {
                                int quality = 200 / KB;
                                File newFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/findlost/camera/" + name);
                                if (!newFile.exists()) {
                                    newFile.createNewFile();
                                }
                                OutputStream outputStream = new FileOutputStream(newFile);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                                file = newFile;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        imageIds = imageIds + " " + name;//记录文件名字
                        map.put(name, file);
                    }
                    DataManager.newItemImage = new ArrayList<String>();
                    item.setIamge_id(imageIds.trim());
                    String json = FastJsonUtil.toJSONString(item);
                    map.put("itemJson", json);
                    helper.uploadItem(map);

                    Looper.loop();
                }
            }).start();
        }

        public void downloadItem() {
            handler = DataManager.myHandler;
            new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();

                    String s = helper.queryLasted();
                    //组装成item

                    List<Item> items = JSON.parseArray(s, Item.class);

                    if (DataManager.datas == null) {
                        DataManager.datas = items;
                        handler.sendEmptyMessage(0x111);
                        Log.i(TAG, "Message 0x111");
                    } else {
                        int i = 0;
                        for (Item item :
                                items) {
                            DataManager.datas.add(i, item);
                        }

                        handler.sendEmptyMessage(0x112);
                        if (DataManager.datas.size() >= 1) {
                            Log.i(TAG, "Message 0x112    " + DataManager.datas.get(0));
                        }
                    }
                    Looper.loop();
                }
            }).start();
        }

        public void downloadItemNext() {
            handler = DataManager.myHandler;
            new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();

                    String s = helper.queryNext();
                    //组装成item
                    Log.i(TAG, "组装items");
                    List<Item> items = JSON.parseArray(s, Item.class);

                    DataManager.datas.addAll(items);
                    handler.sendEmptyMessage(0x116);
                    Looper.loop();
                }
            }).start();
        }

        public void downloadItemById(final String createByID) {
            handler = DataManager.myHandler;
            new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();

                    String s = helper.downloadItemById(createByID);

                    List<Item> items = JSON.parseArray(s, Item.class);
                    if (items != null) {
                        DataManager.push = items;
                        Log.i(TAG, "查询到的总条数    " + items.size());//打印上传的json字符串
                        handler.sendEmptyMessage(0x120);
                    }
                    Looper.loop();
                }
            }).start();
        }

        /**
         * 根据关键词搜索itemss
         *
         * @param key
         */
        public void downloadItemByKey(final String key, final Handler handler) {
            new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();
                    ArrayList<Item> items = new ArrayList<>();
                    try {
                        String s = helper.downloadItemByKey(key);
                        items.addAll(JSON.parseArray(s, Item.class));
                        Message mes = new Message();
                        mes.what = 0x110;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("items", items);
                        mes.setData(bundle);
                        handler.sendMessage(mes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Looper.loop();
                }
            }).start();
        }


        public void remarkItem(final int itemID) {
            new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();
                    ArrayList<Item> items = new ArrayList<>();
                    try {
                        String s = helper.remarkItem(itemID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Looper.loop();
                }
            }).start();
        }

        public void deleteItem(final int itemID) {
            new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();
                    ArrayList<Item> items = new ArrayList<>();
                    try {
                        String s = helper.deleteItem(itemID);
                        items.addAll(JSON.parseArray(s, Item.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Looper.loop();
                }
            }).start();
        }
    }


}
