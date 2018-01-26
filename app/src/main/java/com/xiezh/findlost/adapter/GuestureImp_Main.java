package com.xiezh.findlost.adapter;

/**
 * Created by xiezh on 2017/11/4.
 */

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiezh.findlost.utils.BitmapCache;
import com.xiezh.findlost.utils.DataManager;

import java.util.List;

public class GuestureImp_Main implements OnGestureListener {

    Context context;
    ImageView view;
    String tag = "me";
    int position;
    TextView count;
    List<String> mData;
    private BitmapCache cache;


    public GuestureImp_Main(Context ct, ImageView vw, int posi, TextView count, List<String> mData) {
        // TODO Auto-generated constructor stub
        context = ct;
        view = vw;
        position = posi;
        this.count = count;
        this.mData = mData;
        cache = BitmapCache.getCache();
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        // TODO Auto-generated method stub
        Log.e(tag, "down-" + "x:" + arg0.getX() + "y:" + arg0.getY());

        return true;
    }

    @Override
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
                           float arg3) {
        // TODO Auto-generated method stub
        int mini_width = 120;
        int mini_speed = 0;
        float distance_right = arg1.getX() - arg0.getX();
        float distance_left = arg0.getX() - arg1.getX();
        float distance_down = arg1.getY() - arg0.getY();
        float distance_up = arg0.getY() - arg1.getY();
        if (distance_right > mini_width && Math.abs(arg2) > mini_speed) {
            Log.e(tag, "onFling-" + "向右滑动    " + position + "   " + mData.size());

            if (position - 1 >= 0) {
                position--;
                cache.setBitmapFromCache(mData.get(position), view);
                DataManager.currentPosition = position;
                count.setText((position + 1) + "/" + mData.size());

                Log.i("curretnPosition", mData.size() + "");
            }
        } else if (distance_left > mini_width && Math.abs(arg2) > mini_speed) {
            Log.e(tag, "onFling-" + "向左滑动    " + position + "   " + (mData.size() - 1));
            if (position + 1 <= mData.size() - 1) {
                position++;
                cache.setBitmapFromCache(mData.get(position), view);

                DataManager.currentPosition = position;
                count.setText((position + 1) + "/" + mData.size());
                Log.i("currentPosition", DataManager.currentPosition + "");
            }
        } else if (distance_down > mini_width && Math.abs(arg2) > mini_speed) {
            Log.e(tag, "onFling-" + "向下滑动");
        } else if (distance_up > mini_width && Math.abs(arg2) > mini_speed) {
            Log.e(tag, "onFling-" + "向上滑动");
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {
        // TODO Auto-generated method stub
        Log.e(tag, "onLongPress-" + "x:" + arg0.getX() + "y:" + arg0.getY());
    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
                            float arg3) {
        // TODO Auto-generated method stub
        Log.e(tag, "onScroll-" + "x:" + arg0.getX() + "y:" + arg0.getY());
        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {
        // TODO Auto-generated method stub
        Log.e(tag, "onShowPress-" + "x:" + arg0.getX() + "y:" + arg0.getY());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        // TODO Auto-generated method stub
        Log.e(tag, "onSingleTapUp-" + "x:" + arg0.getX() + "y:" + arg0.getY());
        return false;
    }

}