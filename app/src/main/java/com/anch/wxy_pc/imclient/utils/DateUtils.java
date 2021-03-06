package com.anch.wxy_pc.imclient.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by wxy-pc on 2015/6/16.
 */
public class DateUtils {
    private static TimeZone timezone = TimeZone.getTimeZone("GMT+8:00");

    public static String getCurTime() {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        sf.setTimeZone(timezone);
        Date curDate = new Date(System.currentTimeMillis());
        return sf.format(curDate);
    }

    /* 获取系统时间 格式为："yyyy/MM/dd " */
    public static String getCurrentDate() {
        Date d = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        sf.setTimeZone(timezone);
        return sf.format(d);
    }

    public static String getCurrDateTime(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日  (E) HH:mm:ss", Locale.TAIWAN);
        sf.setTimeZone(timezone);
        return sf.format(d);
    }

    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static Long getCurrTime() {
        return System.currentTimeMillis();
    }

    // 使用系统当前日期加以调整作为照片的名称
    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }
}
