package com.xiezh.findlost.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiezh.findlost.activity.TalkActivity;
import com.xiezh.findlost.domain.Item;
import com.xiezh.findlost.utils.BitmapCache;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.fragmentdemo2.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xiezh on 2017/10/25.
 */

public class PushAdapter extends BaseAdapter {

    private List<Item> items;
    private Context mContext;
    private ArrayList<String> imageIDs;
    private BitmapCache cache;

    public PushAdapter(Context mContext) {
        this.mContext = mContext;
        this.items = DataManager.push;
        cache = BitmapCache.getCache();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.item_list_remark = (TextView) convertView.findViewById(R.id.item_list_remark);
            viewHolder.item_user_name = (TextView) convertView.findViewById(R.id.item_user_name);
            viewHolder.item_time = (TextView) convertView.findViewById(R.id.item_time);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout);

            viewHolder.item_user_head_image = (ImageView) convertView.findViewById(R.id.item_user_head_image);
            viewHolder.item_gridView = (GridView) convertView.findViewById(R.id.item_image_grid);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.item_list_remark.setText(items.get(position).getRemark());
        viewHolder.item_time.setText(items.get(position).getDate());
        viewHolder.item_user_name.setText(items.get(position).getUserName());
        cache.setBitmapFromCache(items.get(position).getCreateUserHeadImage(), viewHolder.item_user_head_image);


        String iamge_id = items.get(position).getIamge_id();
        String[] split = iamge_id.split(" ");
        imageIDs = new ArrayList<>();
        for (String id :
                split) {
            imageIDs.add(id);
        }

        //Toast.makeText(mContext,"传入ids",Toast.LENGTH_SHORT).show();
        final ImageAdapter imageAdapter = new ImageAdapter(imageIDs, mContext);
        viewHolder.item_gridView.setAdapter(imageAdapter);
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!items.get(position).getCreateByID().equals(DataManager.userInfo.getUserID())) {
                    Toast.makeText(mContext, items.get(position).getCreateByID() + "" + DataManager.userInfo.getUserID(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, TalkActivity.class);
                    intent.putExtra("item", items.get(position));
                    mContext.startActivity(intent);
                }
            }
        });
        return convertView;
    }


    private class ViewHolder {
        TextView item_list_remark;
        GridView item_gridView;
        ImageView item_user_head_image;
        TextView item_user_name;
        TextView item_time;
        LinearLayout linearLayout;
    }
}
