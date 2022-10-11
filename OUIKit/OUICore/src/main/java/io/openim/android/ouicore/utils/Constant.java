package io.openim.android.ouicore.utils;

import java.util.ArrayList;

import io.openim.android.ouicore.im.IM;
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.Message;

public class Constant {
//    public static final String DEFAULT_IP = "web.bytechat-test.com";

//    //IM sdk api地址
//    public static final String IM_API_URL = "http://" + DEFAULT_IP + ":10002";
//    //登录注册手机验 证服务器地址
//    public static final String APP_AUTH_URL = "http://" + DEFAULT_IP + ":10004";
//    //web socket
//    public static final String IM_WS_URL = "ws://" + DEFAULT_IP + ":10001";

    public static final String DEFAULT_IP = "web.bytechat-test.com";

//    public static final String DEFAULT_IP = "dev.peakim.com";

    //IM sdk api地址
    public static final String IM_API_URL = "http://" + DEFAULT_IP + "/api/";
    //登录注册手机验 证服务器地址
    public static final String APP_AUTH_URL = "http://" + DEFAULT_IP + "/demo/";
    //web socket
    public static final String IM_WS_URL = "ws://" + DEFAULT_IP + "/msg_gateway";

    public static final String K_NAME = "name";

    public static final String CONVERSATION_ID = "conversationID";

    //存储音频的文件夹
    public static final String AUDIODIR = IM.getStorageDir() + "/audio/";
    //视频存储文件夹
    public static final String VIDEODIR = IM.getStorageDir() + "/video/";
    //图片存储文件夹
    public static final String PICTUREDIR = IM.getStorageDir() + "/picture/";

    public static class Event {
        //已读数量变化
        public static final int READ_CHANGE = 10001;
    }

    //会话类型
    public static class SessionType {
        public static final int SINGLE_CHAT = 1;
        public static final int GROUP_CHAT = 2;
    }

    public static final String ID = "Id";
    public static final String GROUP_ID = "group_id";
    public static final String IS_PERSON = "is_person";
    public static final Message START_MSG = null;

    /**
     * 发送状态
     */
    public static class Send_State {
        //发送中...
        public static final int SENDING = 1;
        //发送失败
        public static final int SEND_FAILED = 3;
    }

    public static ArrayList<FriendInfo> FREQUENT_CONTACTS_LIST = new ArrayList<>();
    public static int COUNTER = 0;
    public static String SCAN_USER_ID = "";
    public static String SCAN_GROUP_ID = "";
}
