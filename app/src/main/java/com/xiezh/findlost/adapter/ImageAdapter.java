package com.xiezh.findlost.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiezh.findlost.activity.ViewImageActivity_Main;
import com.xiezh.findlost.utils.BitmapCache;
import com.xiezh.fragmentdemo2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiezh on 2017/10/31.
 */

public class ImageAdapter extends BaseAdapter {

    private List<String> mData;
    private Context mContext;
    private ImageView imageView;
    private Dialog dialog;
    private View inflate;
    private BitmapCache cache;

    public ImageAdapter(List<String> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_imag, parent, false);
            viewHolder.item_image = (ImageView) convertView.findViewById(R.id.item_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ImageAdapter.ViewHolder) convertView.getTag();
        }

        //viewHolder.item_image.setImageBitmap();
        //viewHolder.item_image.setTag();
        //设置imageview的图片
        if (mData.get(position).equals("")) {
            viewHolder.item_image.setLayoutParams(new LinearLayout.LayoutParams(20, 20));
        } else {
            cache = BitmapCache.getCache();
            //Toast.makeText(mContext,"设置 "+position ,Toast.LENGTH_SHORT).show();
            if (!mData.get(position).equals("") | mData.get(position) != null) {
                cache.setBitmapFromCache(mData.get(position), viewHolder.item_image);
                viewHolder.item_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show(position);
                    }
                });
            }

        }
        return convertView;
    }

    public void show(int position) {
        Intent intent = new Intent(mContext, ViewImageActivity_Main.class);
        intent.putExtra("position", position);
        intent.putStringArrayListExtra("mData", (ArrayList<String>) mData);
        mContext.startActivity(intent);



        /*dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);

        //填充对话框的布局
        inflate = LayoutInflater.from(mContext).inflate(R.layout.dialog_center, null);
        //初始化控件
        imageView = inflate.findViewById(R.id.image);
        cache.setBitmapFromCache(mData.get(position),imageView);

        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;//设置Dialog距离底部的距离
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框*/
    }

    private class ViewHolder {
        ImageView item_image;
    }

}
