package com.xiezh.findlost.view;

import android.content.Context;
import android.widget.GridView;

/**
 * Created by xiezh on 2017/10/31.
 */

public class MyGridView extends GridView {

    public MyGridView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
