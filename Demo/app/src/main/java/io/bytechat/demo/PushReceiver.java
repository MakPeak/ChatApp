package io.bytechat.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;
import io.bytechat.demo.ui.SplashActivity;

/**
 * Created By Mak
 * Custom Class for Receiving Notifications from JPush
 *
 * function onNotifyMessageArrived will be called when the system receives
 * @see #onNotifyMessageArrived(Context, NotificationMessage)
 *
 * function onNotifyMessageOpened called notification open click event
 * @see #onNotifyMessageOpened(Context, NotificationMessage)
 * */
public class PushReceiver extends JPushMessageReceiver {
    private static final String TAG = "PushReceiver";

    public PushReceiver() {
        super();
        System.out.println("PushReceiver::::super()");

    }

    @Override
    public void onInAppMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onInAppMessageArrived(context, notificationMessage);
        System.out.println("PushReceiver :::: onInAppMessageArrived");
    }

    @Override
    public void onConnected(Context context, boolean b) {
        super.onConnected(context, b);
        System.out.println("PushReceiver::::onConnected");
    }

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        System.out.println("PushReceiver::::onMessage Received:: " + customMessage.message);

    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
        System.out.println("PushReceiver::::onNotifyMessageArrived:: " + notificationMessage.notificationTitle);
        System.out.println("PushReceiver::::Content:: " + notificationMessage.toString());
    }


    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
//        Bundle bundle = new Bundle();
//        bundle.putString();

        Intent mIntent = new Intent(context, SplashActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
        /*

            var intent = Intent(MyApplication.getContext(), MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("data", json.toString());
            MyApplication.getContext()?.startActivity(intent);
        */
    }

    //    @Override
//    public void onReceive(Context context, Intent intent) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Action: " + intent.getAction() + "\n");
//        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
//        String log = sb.toString();
//        Log.d(TAG, log);
//        Bundle bundle = intent.getExtras();
//        Toast.makeText(context, bundle.toString(), Toast.LENGTH_LONG).show();
//    }
}
