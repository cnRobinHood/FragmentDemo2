package com.xiezh.findlost.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiezh.findlost.adapter.CameraImageAdapter;
import com.xiezh.findlost.adapter.GuestureImp;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.fragmentdemo2.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ViewImageActivity extends BaseActivity implements View.OnTouchListener {

    private ImageView imageView;
    private TextView back;
    private TextView count;
    private TextView delete;
    private int position;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_view_image);
        super.onCreate(savedInstanceState);
        position = this.getIntent().getIntExtra("position", 0);

        init();
    }

    void init() {
        imageView = (ImageView) findViewById(R.id.view_image);
        back = (TextView) findViewById(R.id.view_image_back);
        count = (TextView) findViewById(R.id.count);
        delete = (TextView) findViewById(R.id.delete);

        changeView(position);
        GuestureImp imp = new GuestureImp(ViewImageActivity.this, imageView, position, count);
        mGestureDetector = new GestureDetector(ViewImageActivity.this, imp);
        imageView.setOnTouchListener(this);
        count.setText((position + 1) + "/" + CameraImageAdapter.mData.size());

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
        Bitmap bitmap = CameraImageAdapter.getData(position);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean temp = mGestureDetector.onTouchEvent(event);
        return temp;
    }
}
