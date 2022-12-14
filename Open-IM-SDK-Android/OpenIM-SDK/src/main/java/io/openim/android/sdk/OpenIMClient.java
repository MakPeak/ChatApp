package io.openim.android.sdk;

import androidx.collection.ArrayMap;

import java.util.Map;

import io.openim.android.sdk.internal.log.LogcatHelper;
import io.openim.android.sdk.listener.BaseImpl;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.listener.OnConnListener;
import io.openim.android.sdk.listener.OnFileUploadProgressListener;
import io.openim.android.sdk.listener._ConnListener;
import io.openim.android.sdk.listener._FileUploadProgressListener;
import io.openim.android.sdk.manager.ConversationManager;
import io.openim.android.sdk.manager.FriendshipManager;
import io.openim.android.sdk.manager.GroupManager;
import io.openim.android.sdk.manager.MessageManager;
import io.openim.android.sdk.manager.OrganizationManager;
import io.openim.android.sdk.manager.SignalingManager;
import io.openim.android.sdk.manager.UserInfoManager;
import io.openim.android.sdk.manager.WorkMomentsManager;
import io.openim.android.sdk.utils.JsonUtil;
import io.openim.android.sdk.utils.ParamsUtil;
import open_im_sdk.Open_im_sdk;

public class OpenIMClient {
    //    public ImManager imManager;
    public ConversationManager conversationManager;
    public FriendshipManager friendshipManager;
    public GroupManager groupManager;
    public MessageManager messageManager;
    public UserInfoManager userInfoManager;
    public SignalingManager signalingManager;
    public WorkMomentsManager workMomentsManager;
    public OrganizationManager organizationManager;

    private OpenIMClient() {
//        imManager = new ImManager();
        conversationManager = new ConversationManager();
        friendshipManager = new FriendshipManager();
        groupManager = new GroupManager();
        messageManager = new MessageManager();
        userInfoManager = new UserInfoManager();
        signalingManager = new SignalingManager();
        workMomentsManager = new WorkMomentsManager();
        organizationManager = new OrganizationManager();
    }

    private static class Singleton {
        private static final OpenIMClient INSTANCE = new OpenIMClient();
    }

    public static OpenIMClient getInstance() {
        return Singleton.INSTANCE;
    }

    /**
     * ?????????sdk
     * ???????????????????????????????????????????????????????????????????????????????????????
     * ??????????????????????????????????????????createSoundMessage????????????????????????createSoundMessageFromFullPath???,
     * ??????????????????????????????dbPath??????????????????????????????????????? apath+"/sound/a.mp3"????????????path????????????/sound/a.mp3???
     * ???????????????????????????????????????????????????????????????????????????????????????
     *
     * @param apiUrl        SDK???API?????????????????????http:xxx:10000
     * @param wsUrl         SDK???web socket??????????????? ws:xxx:17778
     * @param storageDir    ????????????????????????
     * @param logLevel      ?????????????????????6
     * @param objectStorage ?????????????????? ??????cos
     * @param listener      SDK???????????????
     * @return boolean   true??????; false??????
     */
    public boolean initSDK(String apiUrl, String wsUrl, String storageDir, int logLevel, String objectStorage, OnConnListener listener) {
        Map<String, Object> map = new ArrayMap<>();
        map.put("platform", 2);
        map.put("api_addr", apiUrl);
        map.put("ws_addr", wsUrl);
        map.put("data_dir", storageDir);
        map.put("log_level", logLevel);
        map.put("object_storage", objectStorage);
        boolean initialized = Open_im_sdk.initSDK(new _ConnListener(listener), ParamsUtil.buildOperationID(), JsonUtil.toString(map));
        LogcatHelper.logDInDebug(String.format("Initialization successful: %s", initialized));
        return initialized;
    }

//    /**
//     * ????????????sdk
//     */
//    public void unInitSDK() {
//        Open_im_sdk.unInitSDK();
//    }

    /**
     * ??????
     *
     * @param uid   ??????id
     * @param token ??????token
     * @param base  callback String
     */
    public void login(OnBase<String> base, String uid, String token) {
        uid = uid+"-2";
        Open_im_sdk.login(BaseImpl.stringBase(base), ParamsUtil.buildOperationID(), uid, token);
    }


    /**
     * ??????
     */
    public void logout(OnBase<String> base) {
        Open_im_sdk.logout(BaseImpl.stringBase(base), ParamsUtil.buildOperationID());
    }

    /**
     * ??????????????????
     */
    public long getLoginStatus() {
        return Open_im_sdk.getLoginStatus();
    }


    public void wakeUp(OnBase<String> base) {
        Open_im_sdk.wakeUp(BaseImpl.stringBase(base), ParamsUtil.buildOperationID());
    }


    /**
     * ????????????????????????
     *
     * @param path ??????
     */
    public void uploadFile(OnFileUploadProgressListener listener, String path) {
        Open_im_sdk.uploadFile(new _FileUploadProgressListener(listener), ParamsUtil.buildOperationID(), path);
    }
}

