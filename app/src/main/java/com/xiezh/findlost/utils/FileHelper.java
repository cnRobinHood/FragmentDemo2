package com.xiezh.findlost.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by xiezh on 2017/11/3.
 */

public class FileHelper {

    private Context mContext;

    public FileHelper() {
    }

    public FileHelper(Context mContext) {
        super();
        this.mContext = mContext;
    }

    /*
    * 这里定义的是一个文件保存的方法，写入到文件中，所以是输出流
    * */
    public void save(String filename, Bitmap bitmap) throws Exception {
        //这里我们使用私有模式,创建出来的文件只能被本应用访问,还会覆盖原文件哦
        FileOutputStream output = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        output.close();         //关闭输出流
    }


    /*
    * 这里定义的是文件读取的方法
    * */
    public Bitmap read(String filename) throws IOException {
        ByteArrayOutputStream out = null;
        Bitmap bitmap = null;
        FileInputStream input;
        try {
            //打开文件输入流
            input = mContext.openFileInput(filename);
            byte[] temp = new byte[1024];
            out = new ByteArrayOutputStream();
            int len = -1;
            while ((len = input.read(temp)) != -1) {
                out.write(temp, 0, len);
            }
            //关闭输入流
            out.close();
            input.close();
            bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().length);
        } catch (IOException e) {
            return bitmap;
        }

        return bitmap;
    }

}