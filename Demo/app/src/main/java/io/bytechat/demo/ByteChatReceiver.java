package io.bytechat.demo;

import static io.openim.android.ouicore.utils.Constant.ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.logging.Logger;

import cn.jpush.android.api.JPushInterface;
import io.bytechat.demo.oldrelease.ui.main.MainActivity;
import io.bytechat.demo.ui.SplashActivity;
import io.openim.android.ouiconversation.ui.ChatActivity;

public class ByteChatReceiver extends BroadcastReceiver {

    private static final String TAG = "ByteChatReceiver";
    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            StringBuilder sb = new StringBuilder();
            sb.append("Action: " + intent.getAction() + "\n");
            sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
            String log = sb.toString();
            Log.d(TAG, log);

            if (null == nm) {
                nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                NotificationCompat.Builder builder =
//                    new NotificationCompat.Builder(context)
//                        .setSmallIcon(R.drawable.splash_logo)
//                        .setContentTitle("Notifications Example")
//                        .setContentText("This is a test notification");
//
//                Intent notificationIntent = new Intent(context, MainActivity.class);
//                PendingIntent contentIntent = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                    contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//                }
//                builder.setContentIntent(contentIntent);
//
//                // Add as notification
//
//                nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                nm.notify(0, builder.build());
            }

            Bundle bundle = intent.getExtras();
//            JSONObject jsonObj = new JSONObject(bundle.get("cn.jpush.android.MESSAGE").toString());
            Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + bundle.toString());
            Toast.makeText(context, bundle.get("cn.jpush.android.MESSAGE").toString(), Toast.LENGTH_LONG).show();
//            Toast.makeText(context, jsonObj.get("from").toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(context, intent.getAction() + "BYTECHAT", Toast.LENGTH_LONG).show();

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                Log.d(TAG, "JPush用户注册成功");
                Toast.makeText(context, "ACTION_REGISTRATION_ID", Toast.LENGTH_LONG).show();

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "接受到推送下来的自定义消息");
                Toast.makeText(context, "ACTION_MESSAGE_RECEIVED", Toast.LENGTH_LONG).show();

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "接受到推送下来的通知");
                Toast.makeText(context, "ACTION_NOTIFICATION_RECEIVED", Toast.LENGTH_LONG).show();
                receivingNotification(context,bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d(TAG, "用户点击打开了通知");
                Toast.makeText(context, "ACTION_NOTIFICATION_OPENED", Toast.LENGTH_LONG).show();
                openNotification(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_CLICK_ACTION.equals(intent.getAction())) {
                Log.d(TAG, "用户点击打开了通知");
                Toast.makeText(context, "ACTION_NOTIFICATION_CLICK_ACTION", Toast.LENGTH_LONG).show();
                openNotification(context, bundle);

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "用户点击打开了通知");
                Toast.makeText(context, "ACTION_RICHPUSH_CALLBACK", Toast.LENGTH_LONG).show();
                openNotification(context, bundle);

            } else {
                Log.d(TAG, "Unhandled intent - " + intent.getAction());
                Toast.makeText(context, "Unhandled intent", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        Toast.makeText(context, "OUT", Toast.LENGTH_LONG).show();
    }

    private void receivingNotification(Context context, Bundle bundle){
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Log.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        Log.d(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.d(TAG, "extras : " + extras);
    }

    private void openNotification(Context context, Bundle bundle){
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        System.out.println("EXTRAS FROM NOTIFICATION: " + extras);
//        Toast.makeText(context, extras.toString(), Toast.LENGTH_LONG).show();
        String myValue = "";
        try {
            JSONObject extrasJson = new JSONObject(extras);
            myValue = extrasJson.optString("myKey");
        } catch (Exception e) {
            Log.w(TAG, "Unexpected: extras is not a valid json", e);
            return;
        }

        Intent mIntent = new Intent(context, SplashActivity.class);
        mIntent.putExtras(bundle);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);

//        if (TYPE_THIS.equals(myValue)) {
//            Intent mIntent = new Intent(context, ThisActivity.class);
//            mIntent.putExtras(bundle);
//            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(mIntent);
//        } else if (TYPE_ANOTHER.equals(myValue)){
//            Intent mIntent = new Intent(context, AnotherActivity.class);
//            mIntent.putExtras(bundle);
//            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(mIntent);
//        }
    }
}
