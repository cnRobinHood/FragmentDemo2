package com.xiezh.findlost.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.xiezh.findlost.adapter.PushAdapter;
import com.xiezh.fragmentdemo2.R;

public class VIewPushActivity extends AppCompatActivity {
    private ListView listView;
    private PushAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_push);

        adapter = new PushAdapter(this);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }

}
