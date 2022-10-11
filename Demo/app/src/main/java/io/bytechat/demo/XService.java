package io.bytechat.demo;

import android.content.Context;
import android.util.Log;

import cn.jpush.android.service.JCommonService;

public class XService extends JCommonService {
    public XService() {
        Log.d("TAG", "Push XService:() ");
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        Log.d("TAG", "Push XService: attachBaseContext");

    }
}
