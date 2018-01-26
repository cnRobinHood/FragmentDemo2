package com.xiezh.findlost.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SearchView;

import com.xiezh.findlost.adapter.MyAdapter;
import com.xiezh.findlost.domain.Item;
import com.xiezh.findlost.service.ItemService;
import com.xiezh.fragmentdemo2.R;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private final int refresh = 0x110;
    String key;
    private SearchView searchView;
    private ListView listView;
    private MyAdapter adapter;
    private ItemService.ItemBinder binder;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case refresh:
                    Bundle data = msg.getData();
                    ArrayList<Item> items = (ArrayList<Item>) data.getSerializable("items");
                    adapter.add(items);
                    adapter.notifyDataSetInvalidated();
                    ;
                    break;
            }
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (ItemService.ItemBinder) service;
            binder.downloadItemByKey(key, handler);
            key = "";
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        adapter = new MyAdapter(this, binder);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        blindView();

        listView.setAdapter(adapter);

    }

    void blindView() {
        searchView = (SearchView) findViewById(R.id.search_view);
        listView = (ListView) findViewById(R.id.list_news_search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                key = query;
                Intent intent = new Intent(SearchActivity.this, ItemService.class);
                bindService(intent, connection, BIND_AUTO_CREATE);
                if (key != null && !"".equals(key) && binder != null) {
                    binder.downloadItemByKey(query, handler);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
