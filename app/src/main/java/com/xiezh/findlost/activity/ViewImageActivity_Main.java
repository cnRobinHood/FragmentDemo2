package com.xiezh.findlost.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiezh.findlost.adapter.GuestureImp_Main;
import com.xiezh.findlost.utils.BitmapCache;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.fragmentdemo2.R;

import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ViewImageActivity_Main extends BaseActivity implements View.OnTouchListener {

    private ImageView imageView;
    private TextView back;
    private TextView count;
    private TextView delete;
    private int position;
    private List<String> mData;
    private GestureDetector mGestureDetector;
    private BitmapCache cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_view_image);
        super.onCreate(savedInstanceState);
        position = this.getIntent().getIntExtra("position", 0);
        mData = this.getIntent().getStringArrayListExtra("mData");
        cache = BitmapCache.getCache();

        init();
    }

    void init() {
        imageView = (ImageView) findViewById(R.id.view_image);
        back = (TextView) findViewById(R.id.view_image_back);
        count = (TextView) findViewById(R.id.count);
        delete = (TextView) findViewById(R.id.delete);

        changeView(position);
        GuestureImp_Main imp = new GuestureImp_Main(ViewImageActivity_Main.this, imageView, position, count, mData);
        mGestureDetector = new GestureDetector(ViewImageActivity_Main.this, imp);
        imageView.setOnTouchListener(this);
        count.setText((position + 1) + "/" + mData.size());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAndRemoveTask();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.cameraImageAdapter.remove(DataManager.currentPosition);
                finish();
            }
        });
    }

    void changeView(int position) {
        cache.setBitmapFromCache(mData.get(position), imageView);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean temp = mGestureDetector.onTouchEvent(event);
        return temp;
    }
}
