package com.anch.wxy_pc.imclient.utils;

import android.os.Environment;

/**
 * Created by wxy-pc on 2015/6/15.
 */
public class Constanst {

    public static final String FileTempDir = Environment.getExternalStorageDirectory().getPath() + "/ImClient/"; // sd卡根目录下
    public static final String FileDb = FileTempDir + "DB/";
    public final static String SQ_NAME = "IM.db";

    public static final String ACCOUNT = "ACCOUNT";
    public static final String PASSWORD = "PASSWORD";

    //当前页判断
    public static int CURRENT_PAGE = 0;
    //聊天页判断
    public static int CHAT_PAGE = 0;
    //当前页值
    public static final int CURRENT_PAGE_VALUE = 1;
    //聊天页值
    public static final int CHAT_PAGE_VALUE = 2;

    //聊天页回传会话页code值
    public static final int RESULT_CODE = 1000;

    //跳转相机请求码
    public static final int TASK_SKIP_CAMERA = 10001;
    //裁剪相片请求码
    public static final int TASK_CUT_PHOTO = 10002;
    //跳转相册请求码
    public static final int TASK_SKIP_PHOTO = 10003;

    //跳转传递当前账户键
    public static final String SKIP_ADD_FRIENT_PASS_USER = "SKIP_ADD_FRIENT_PASS_USER";
}
