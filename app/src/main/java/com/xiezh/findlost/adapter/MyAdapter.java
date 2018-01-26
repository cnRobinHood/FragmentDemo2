package com.xiezh.findlost.adapter;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
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
import com.xiezh.findlost.service.ItemService;
import com.xiezh.findlost.utils.BitmapCache;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.fragmentdemo2.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xiezh on 2017/10/25.
 */

public class MyAdapter extends BaseAdapter {

    private List<Item> items;
    private Context mContext;
    private ArrayList<String> imageIDs;
    private BitmapCache cache;
    private ItemService.ItemBinder binder;

    public MyAdapter(Context mContext, FragmentManager fManager, ItemService.ItemBinder binder) {
        this.mContext = mContext;
        this.items = DataManager.datas;
        cache = BitmapCache.getCache();
        this.binder = binder;
    }

    public MyAdapter(Context mContext, ItemService.ItemBinder binder) {
        this.mContext = mContext;
        cache = BitmapCache.getCache();
        items = new ArrayList<>();
        this.binder = binder;
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
        if (items.get(position).getStatus() == 1) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.textGray));
        }

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
                    //Toast.makeText(mContext,items.get(position).getCreateByID() + "" +DataManager.userInfo.getUserID(),Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, TalkActivity.class);
                    intent.putExtra("item", items.get(position));
                    mContext.startActivity(intent);
                }
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showListDialog(position);
                return false;
            }
        });
        return convertView;
    }

    public void add(Item item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(0, item);
        notifyDataSetChanged();
    }

    /**
     * 只有在搜索时使用了这个方法
     *
     * @param items
     */
    public void add(List<Item> items) {
        this.items.clear();
        this.items.addAll(items);
        Log.i("SearchActivity", "添加" + items.toString());
        //notifyDataSetChanged();
    }

    private void showListDialog(final int position) {
        final String[] strings = {"标记为已找到", "删除"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(mContext);
        listDialog.setTitle("提示");
        listDialog.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // which 下标从0开始
                // ...To-do
                Toast.makeText(mContext,
                        "你点击了" + strings[which],
                        Toast.LENGTH_SHORT).show();
                switch (which) {
                    case 0:         //标记
                        binder.remarkItem(items.get(position).getItemID());
                        items.get(position).setStatus(0);
                        refresh();
                        break;
                    case 1:         //删除
                        binder.deleteItem(items.get(position).getItemID());
                        items.remove(position);
                        refresh();
                        break;
                }
            }
        });
        listDialog.show();
    }

    public void refresh() {
        notifyDataSetChanged();
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
