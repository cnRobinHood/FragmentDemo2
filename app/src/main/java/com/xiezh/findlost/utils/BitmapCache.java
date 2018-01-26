package com.xiezh.findlost.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.xiezh.findlost.https.HttpHelper;
import com.xiezh.fragmentdemo2.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiezh on 2017/11/8.
 */

public class BitmapCache {

    public static final int maxMemory = (int) (Runtime.getRuntime().maxMemory());
    public static List<String> nameList = new ArrayList<>();
    private static LruCache<String, Bitmap> cache;
    private static HttpHelper httpHelper;
    private static BitmapCache myCache = new BitmapCache();

    static {
        cache = new LruCache<String, Bitmap>(maxMemory / 8) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (value != null)
                    return value.getByteCount() / 1024;
                return 0;
            }
        };

        httpHelper = new HttpHelper();
    }

    private BitmapCache() {
    }

    public static BitmapCache getCache() {
        return myCache;
    }

    public static void clear() {
        for (String key : cache.snapshot().keySet()) {
            cache.remove(key);
        }
    }

    public static void addBitMap(String key, Bitmap bitmap) {
        cache.put(key, bitmap);
    }

    public static double getSize() {
        return cache.size() / 1024.00;
    }

    /**
     * 从缓存中获取图片 缓存中不存在就在网络上获取
     *
     * @param key
     * @return
     */
    public void setBitmapFromCache(final String key, final ImageView imageView) {
        Log.i("cache", "设置图片" + key);
        final Bitmap bitmap;
        bitmap = cache.get(key);
        if (bitmap == null) {
            Thread thread = new Thread() {
                @Override
                public void run() {

                    final Bitmap bitmap2 = httpHelper.downloadfile(key, "itemimage");
                    ;
                    if (bitmap2 != null) {
                        cache.put(key, bitmap2);
                        nameList.add(key);
                        Log.i("cache", "加入了图片" + key);
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("cache", "设置了图片" + key);

                                if (bitmap2 != null) {
                                    imageView.setImageBitmap(bitmap2);
                                } else {
                                    imageView.setImageResource(R.mipmap.error);
                                }
                            }
                        });
                    }
                }
            };
            thread.start();
        } else {

            Log.i("cache", "存在" + key);
            //如果缓存中存在图片直接设置
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    //imageView.setImageBitmap(bitmap);
                    imageView.setImageBitmap(cache.get(key));
                }
            });
        }
    }

    public void setHeadBitmapFromCache(final String key, final ImageView imageView) {
        Log.i("cache", "设置图片" + key);
        final Bitmap bitmap;
        bitmap = cache.get(key);
        if (bitmap == null) {
            Thread thread = new Thread() {
                @Override
                public void run() {

                    final Bitmap bitmap2 = httpHelper.downloadfile(key, "/userhead");
                    ;
                    if (bitmap2 != null) {
                        cache.put(key, bitmap2);
                        nameList.add(key);
                        Log.i("cache", "加入了图片" + key);
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("cache", "设置了图片" + key);
                                if (bitmap2 != null) {
                                    imageView.setImageBitmap(bitmap2);
                                } else {
                                    imageView.setImageResource(R.mipmap.error);
                                }
                            }
                        });
                    }
                }
            };
            thread.start();
        } else {

            Log.i("cache", "存在" + key);
            //如果缓存中存在图片直接设置
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    //imageView.setImageBitmap(bitmap);
                    imageView.setImageBitmap(cache.get(key));
                }
            });
        }
    }

    public void save(Context context) {
        try {
            FileOutputStream out = context.openFileOutput("bitmapList.in", Context.MODE_PRIVATE);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

            //写入图片名字
            for (String name : nameList) {
                writer.write(JSON.toJSONString(name));
                writer.newLine();
            }
            File cacheDir = context.getCacheDir();
            File file = null;
            FileOutputStream outFile = null;
            Bitmap bitmap = null;
            //把缓存中的图片写到本地缓存
            for (String name : nameList) {
                file = new File(cacheDir, name);
                //不存在就创建，文件并写入，存在就不做缓存
                if (!file.exists()) {
                    file.createNewFile();
                    outFile = new FileOutputStream(file);
                    bitmap = cache.get(name);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, outFile);
                    outFile.flush();
                    outFile.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
