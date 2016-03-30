package com.doesnotscale.android.getdonetoday.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.doesnotscale.android.getdonetoday.R;

/**
 * Created by ezaneski on 3/30/16.
 */
public class DailyNotificationAlarmReceiver extends BroadcastReceiver {
    public static final String TAG = DailyNotificationAlarmReceiver.class.getSimpleName();
    public static final int REQUEST_CODE = 1;
    public static final int NOTIFICATION_REQUEST_CODE = 2;

    public static PendingIntent newIntent(Context context) {
        Intent intent = new Intent(context, DailyNotificationAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    public void showNotification(Context context) {
        Intent intent = new Intent(context, TodayListActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, NOTIFICATION_REQUEST_CODE, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_my_launcher)
                .setContentTitle("Get Done Today")
                .setContentText("What do you want to do today?");
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_REQUEST_CODE, mBuilder.build());
    }
}
