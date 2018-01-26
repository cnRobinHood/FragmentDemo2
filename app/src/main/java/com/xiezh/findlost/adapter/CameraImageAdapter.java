package com.xiezh.findlost.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xiezh.findlost.activity.ViewImageActivity;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.fragmentdemo2.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by xiezh on 2017/11/4.
 */

public class CameraImageAdapter extends BaseAdapter {

    public static ArrayList<Bitmap> mData = new ArrayList<>();
    ;
    private Context mContext;

    public CameraImageAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public static Bitmap getData(int position) {
        return mData.get(position);
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
        CameraImageAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_imag2, parent, false);
            viewHolder = new CameraImageAdapter.ViewHolder();
            viewHolder.item_image = (ImageView) convertView.findViewById(R.id.item_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CameraImageAdapter.ViewHolder) convertView.getTag();
        }
        //viewHolder.item_image.setImageBitmap();
        Bitmap bitmap = mData.get(position);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        viewHolder.item_image.setImageBitmap(mData.get(position));

        viewHolder.item_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewImageActivity.class);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    /**
     * 在gridview中添加一个图片
     *
     * @param bitmap
     */
    public void add(Bitmap bitmap) {
        if (mData.size() >= 3) {
            mData.remove(2);
            DataManager.newItemImage.remove(2);
        }
        int KB = bitmap.getByteCount() / 1024;//bitmap的大小，压缩到200kb左右
        if (KB > 200) {
            int quality = 200 / KB;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

            byte[] data = outputStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        }

        mData.add(bitmap);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (position >= mData.size()) {
            position = mData.size() - 1;
        }
        mData.remove(position);
        DataManager.newItemImage.remove(position);
        notifyDataSetChanged();
    }

    public void removeAll() {
        mData.clear();
        notifyDataSetChanged();
        Log.i("CameraImageAdapter", "清除所有图片信息");
    }

    private class ViewHolder {
        ImageView item_image;
    }

}
