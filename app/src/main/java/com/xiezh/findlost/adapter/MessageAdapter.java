package com.xiezh.findlost.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiezh.findlost.activity.TalkActivity;
import com.xiezh.findlost.domain.Message;
import com.xiezh.findlost.utils.BitmapCache;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.fragmentdemo2.R;

import java.util.Iterator;
import java.util.List;


/**
 * Created by xiezh on 2017/10/25.
 */

public class MessageAdapter extends BaseAdapter {

    private List<Message> messages;
    private Context mContext;

    public MessageAdapter(Context context) {
        mContext = context;
        messages = DataManager.message;
    }

    @Override
    public int getCount() {
        if (messages == null) {
            return 0;
        }
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_message, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.lasted_time = (TextView) convertView.findViewById(R.id.lasted_time);
            viewHolder.username = (TextView) convertView.findViewById(R.id.username);
            viewHolder.lasted_message = (TextView) convertView.findViewById(R.id.lasted_message);

            viewHolder.user_head = (ImageView) convertView.findViewById(R.id.head_image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.lasted_time.setText(messages.get(position).getDate());
        viewHolder.username.setText(messages.get(position).getToUser().getUserName());
        viewHolder.lasted_message.setText((String) messages.get(position).getMessageList().get(messages.get(position).getMessageList().size() - 1));

        BitmapCache cache = BitmapCache.getCache();

        if (messages.get(position) != null) {
            cache.setHeadBitmapFromCache(messages.get(position).getToUser().getHeadImag(), viewHolder.user_head);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(mContext, TalkActivity.class);
                intent.putExtra("userinfo", messages.get(position).getToUser());
                mContext.startActivity(intent);


            }
        });

        return convertView;
    }

    public void add(Message message) {

        int i = 0;
        Message exitMessage;
        Iterator<Message> iterator = messages.iterator();
        while (iterator.hasNext()) {
            exitMessage = iterator.next();
            if (message.getToUser().getUserID().equals(exitMessage.getToUser().getUserID())) {
                messages.remove(i);
            }
        }
        messages.add(0, message);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView user_head;
        TextView username;
        TextView lasted_message;
        TextView lasted_time;
    }

}
