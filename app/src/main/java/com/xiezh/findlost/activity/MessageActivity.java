package com.xiezh.findlost.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xiezh.fragmentdemo2.R;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        String username = savedInstanceState.getString("username");

    }
}
