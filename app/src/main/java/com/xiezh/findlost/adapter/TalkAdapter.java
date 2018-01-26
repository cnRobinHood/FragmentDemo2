package com.xiezh.findlost.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiezh.findlost.domain.Message;
import com.xiezh.findlost.utils.BitmapCache;
import com.xiezh.fragmentdemo2.R;

import java.util.List;


/**
 * Created by xiezh on 2017/10/25.
 */

public class TalkAdapter extends BaseAdapter {

    private static final int send = 0;
    private static final int get = 1;
    private BitmapCache cache;
    private List<Message> messages;
    private Context mContext;

    public TalkAdapter(Context context, List messages) {
        mContext = context;
        this.messages = messages;
        cache = BitmapCache.getCache();
    }

    @Override
    public int getCount() {
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
    public int getItemViewType(int position) {

        if (messages.get(position).isType()) {
            return send;
        } else {
            return get;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder1 = null;
        ViewHolder viewHolder2 = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case get:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.talk_message_a, parent, false);
                    viewHolder2 = new ViewHolder();

                    viewHolder2.head = convertView.findViewById(R.id.head_image);
                    viewHolder2.txt = convertView.findViewById(R.id.txt);
                    cache.setBitmapFromCache(messages.get(position).getFromId() + ".png", viewHolder2.head);
                    convertView.setTag(R.id.MessageGet, viewHolder2);
                    break;

                case send:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.talk_messsage_b, parent, false);
                    viewHolder1 = new ViewHolder();

                    viewHolder1.head = convertView.findViewById(R.id.head_image_b);
                    viewHolder1.txt = convertView.findViewById(R.id.txt_b);
                    cache.setBitmapFromCache(messages.get(position).getFromId() + ".png", viewHolder1.head);
                    convertView.setTag(R.id.MessageSend, viewHolder1);
                    break;
            }


        } else {
            switch (type) {
                case get:
                    viewHolder2 = (ViewHolder) convertView.getTag(R.id.MessageGet);
                    break;
                case send:
                    viewHolder1 = (ViewHolder) convertView.getTag(R.id.MessageSend);
                    break;
            }
        }
        //Toast.makeText(mContext,messages.get(position).getToUser().getHeadImag(),Toast.LENGTH_SHORT).show();

        Message message = messages.get(position);
        if (message != null) {
            StringBuilder sb = new StringBuilder();
            for (String str :
                    message.getMessageList()) {
                sb.append(str + "\r\n");
            }

            switch (type) {
                case get:
                    viewHolder2.txt.setText(sb.toString());
                    break;
                case send:
                    viewHolder1.txt.setText(sb.toString());
                    break;
            }
        }

        return convertView;
    }

    public void add(Message message) {
        messages.add(message);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView head;
        TextView txt;
    }
}