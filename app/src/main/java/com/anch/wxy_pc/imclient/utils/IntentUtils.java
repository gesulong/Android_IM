package com.anch.wxy_pc.imclient.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;


/**
 * Created by wxy-pc on 2015/6/23.
 */
public class IntentUtils {

    /**
     * 无参跳转
     *
     * @param activity
     * @param object
     * @param flag
     */
    public static void skip(Activity activity, Class object, boolean flag) {
        Intent intent = new Intent(activity, object);
        activity.startActivity(intent);
        if (flag) activity.finish();
    }

    /**
     * 有参跳转
     *
     * @param activity
     * @param object
     * @param flag
     */
    public static void skip(Activity activity, Class object, String key, String value, boolean flag) {
        Intent intent = new Intent(activity, object);
        intent.putExtra(key, value);
        activity.startActivity(intent);
        if (flag) activity.finish();
    }

    /**
     * 跳转照相机
     *
     * @param activity
     * @param imageUri
     */
    public static void skipCamera(Activity activity, Uri imageUri, int requstCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intent, requstCode);
    }

    /**
     * 跳转相册
     *
     * @param activity
     * @param requstCode
     */
    public static void skipPhoto(Activity activity, int requstCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, requstCode);
    }

    /**
     * 截图方法
     *
     * @param uri
     * @param requestCode
     */
    public static void cropImageUri(Activity activity, Uri uri, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
//        intent.putExtra("scale", true);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, requestCode);
    }

}
