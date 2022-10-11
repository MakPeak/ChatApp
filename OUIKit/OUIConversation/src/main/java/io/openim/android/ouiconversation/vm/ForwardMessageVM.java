package io.openim.android.ouiconversation.vm;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.github.promeg.pinyinhelper.Pinyin;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.openim.android.ouiconversation.entity.ExUserInfo;
import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.utils.Common;
import io.openim.android.ouicore.utils.L;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.listener.OnMsgSendCallback;
import io.openim.android.sdk.models.BusinessCard;
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.GroupInfo;
import io.openim.android.sdk.models.Message;
import io.openim.android.sdk.models.OfflinePushInfo;
import io.openim.android.sdk.models.SearchResult;
import io.openim.android.sdk.models.UserInfo;

public class ForwardMessageVM extends BaseViewModel<ForwardMessageVM.ViewAction> {

    public MutableLiveData<List<ExUserInfo>> exUserInfo = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Boolean> groupInviteResult = new MutableLiveData<>();
    public MutableLiveData<Message> sendMessageResult = new MutableLiveData<>();
    public String groupID = " " , otherSideID = " ";
    public MutableLiveData<List<String>> letters = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<List<FriendInfo>> selectedFriendInfo = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<List<GroupInfo>> groupJoinedInfo = new MutableLiveData<>();
    public static MutableLiveData<String> searchForward = new MutableLiveData<>("");

    public void getAllFriend() {
        OpenIMClient.getInstance().friendshipManager.getFriendList(new OnBase<List<UserInfo>>() {
            @Override
            public void onError(int code, String error) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(List<UserInfo> data) {
                if (data.isEmpty()) return;
                List<ExUserInfo> exInfos = new ArrayList<>();
                List<ExUserInfo> otInfos = new ArrayList<>();
                for (UserInfo datum : data) {
                    ExUserInfo exUserInfo = new ExUserInfo();
                    exUserInfo.userInfo = datum;
                    String letter = Pinyin.toPinyin(exUserInfo.userInfo.getFriendInfo().getNickname().charAt(0));
                    if (!Common.isAlpha(letter)) {
                        exUserInfo.sortLetter = "#";
                        otInfos.add(exUserInfo);
                    } else {
                        exUserInfo.sortLetter = letter;
                        exUserInfo.sortLetter = String.valueOf(letter.charAt(0)).toLowerCase(Locale.ROOT);
                        exInfos.add(exUserInfo);
                    }
                }
                for (ExUserInfo userInfo : exInfos) {
                    if (!letters.getValue().contains(userInfo.sortLetter.charAt(0)))
                        letters.getValue().add(String.valueOf(userInfo.sortLetter.charAt(0)));
                }
                if (!otInfos.isEmpty())
                    letters.getValue().add("#");
                letters.setValue(letters.getValue());

                exUserInfo.getValue().addAll(exInfos);
                exUserInfo.getValue().addAll(otInfos);
                exUserInfo.getValue().sort(new Comparator<ExUserInfo>() {
                    @Override
                    public int compare(ExUserInfo exUserInfo, ExUserInfo t1) {
                        return exUserInfo.sortLetter.compareTo(t1.sortLetter);
                    }
                });
                exUserInfo.setValue(exUserInfo.getValue());
            }
        });
    }

    public void getJoinedGroupList() {

        OpenIMClient.getInstance().groupManager.getJoinedGroupList(new OnBase<List<GroupInfo>>() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onSuccess(List<GroupInfo> data) {
                if (!data.isEmpty())
                    groupJoinedInfo.setValue(data);

                System.out.println("getJoinedGroupList onSuccess :"+data);
            }
        });
    }
    public void sendMsg(Message msg) {
        System.out.println("otherSideID + " + otherSideID + " groupID " + groupID);
        OfflinePushInfo offlinePushInfo = new OfflinePushInfo();  // 离线推送的消息备注；不为null
        OpenIMClient.getInstance().messageManager.sendMessage(new OnMsgSendCallback() {
            @Override
            public void onError(int code, String error) {
                L.e("onError in sendMsg function" + code + " " + error);
            }

            @Override
            public void onProgress(long progress) {
                L.e("onProgress in sendMsg function");
            }

            @Override
            public void onSuccess(Message message) {
                sendMessageResult.setValue(message);
            }
        }, msg, otherSideID, groupID, offlinePushInfo);

    }
    public Message createForwardMessage(Message msg){
        return  OpenIMClient.getInstance().messageManager.createForwardMessage(msg);
    }
    public Message createCardMessage(BusinessCard businessCard){
        Gson gson = new Gson();
        String json = gson.toJson(businessCard);
        return  OpenIMClient.getInstance().messageManager.createCardMessage(json);
    }
    public interface ViewAction extends io.openim.android.ouicore.base.IView {

        void onError(String msg);

        void onSuccess(Object o);

    }
}
