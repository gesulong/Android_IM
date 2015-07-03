package com.anch.wxy_pc.imclient.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.anch.wxy_pc.imclient.R;

/**
 * Created by wxy-pc on 2015/7/1.
 */
public class AddFriendNotification {

    private NotificationManager manager;
    private Context mContext;

    public AddFriendNotification(Context context) {
        this.mContext = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showNotification(String firendName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setTicker("请求添加好友");
        builder.setSmallIcon(R.mipmap.im_icon);
        builder.setContentTitle("添加好友");
        builder.setContentText(firendName + "与您建立了好友关系，你们现在已经是好友了。");
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.im_icon));
        builder.setOngoing(true);
        builder.setNumber(8);
        manager.notify(1, builder.build());
    }
}
