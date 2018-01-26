package com.xiezh.findlost.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by xiezh on 2017/11/4.
 */

public class UUIDUtils {
    static private int count = 0;

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        return s.replace("-", "");
    }

    public static String getUUIDByTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        count++;
        return formatter.format(curDate) + "_" + count;
    }
}
