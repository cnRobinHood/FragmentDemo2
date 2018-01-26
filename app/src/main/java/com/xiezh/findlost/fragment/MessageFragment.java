package com.xiezh.findlost.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.xiezh.findlost.adapter.MessageAdapter;
import com.xiezh.findlost.domain.Message;
import com.xiezh.fragmentdemo2.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by xiezh on 2017/10/29.
 */

@SuppressLint({"NewApi", "ValidFragment"})
public class MessageFragment extends Fragment {
    private ListView list_view;
    private MessageAdapter adapter;
    private Context context;

    public MessageFragment() {
    }

    public MessageFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_messagelist, container, false);
        list_view = view.findViewById(R.id.list_message);

        adapter = new MessageAdapter(context);

        list_view.setAdapter(adapter);
        return view;
    }

    /**
     * 消息界面添加一条或者刷新一条消息
     *
     * @param message
     */
    public void add(Message message) {
        if (message != null) {
            if (adapter == null) {
                adapter = new MessageAdapter(context);

            }
            adapter.add(message);
            writeMessage(message);
        } else {
            Log.i("message adapter", "传过来是空的");
        }
    }

    public void writeMessage(Message message) {
        //写入输入输出
        FileOutputStream out = null;
        BufferedWriter bfo = null;
        try {

            File cacheDir = getActivity().getCacheDir();
            File userCache = new File(cacheDir, message.getFromId() + ".cache");
            if (!userCache.exists()) {
                try {
                    userCache.createNewFile();
                } catch (IOException e) {
                    Toast.makeText(getContext(), "创建缓存失败，将不会记录消息", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            out = new FileOutputStream(userCache, true);
            bfo = new BufferedWriter(new OutputStreamWriter(out));

            bfo.write(JSON.toJSONString(message));
            bfo.newLine();
            bfo.flush();

            bfo.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}
