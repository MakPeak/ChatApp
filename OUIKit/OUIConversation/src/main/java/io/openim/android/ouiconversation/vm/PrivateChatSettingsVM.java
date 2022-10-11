package io.openim.android.ouiconversation.vm;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.entity.MsgConversation;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.models.ConversationInfo;
import io.openim.android.sdk.models.NotDisturbInfo;
import io.openim.android.sdk.models.SearchResult;
import io.openim.android.sdk.models.UserInfo;

public class PrivateChatSettingsVM extends BaseViewModel<PrivateChatSettingsVM.ViewAction>{
    private static final String TAG="PrivateChatSettingsVM";
    public String userID = "";
    public MutableLiveData<List<UserInfo>> userInfo = new MutableLiveData<>();
    public MutableLiveData<List<NotDisturbInfo>> DNDResponse = new MutableLiveData<>();
    public MutableLiveData<ConversationInfo> conversationInfoResponse = new MutableLiveData<>();
    public MutableLiveData<String> chatID = new MutableLiveData<>();
    public MutableLiveData<String> clearChatHistoryResponse = new MutableLiveData<>();
    public MutableLiveData<String> pinDataResponse = new MutableLiveData<>();
    public MutableLiveData<String> deleteFriend = new MutableLiveData<>();
    public MutableLiveData<String> oneConversationPrivateChat = new MutableLiveData<>();
    public static boolean isConvCleared = false ;

    public void searchPerson() {
        List<String> uidList = new ArrayList<>(); // 用户ID集合
        uidList.add(userID);
        System.out.println("searchContent" + userID);
        OpenIMClient.getInstance().userInfoManager.getUsersInfo(new OnBase<List<UserInfo>>() {
            @Override
            public void onError(int code, String error) {
                userInfo.setValue(null);
            }

            @Override
            public void onSuccess(List<UserInfo> data) {
                userInfo.setValue(data);
            }
        }, uidList);
    }
    public void changePin(boolean isPin) {
        System.out.println("fullChatID" + chatID.getValue());
        OpenIMClient.getInstance().conversationManager.pinConversation(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                System.out.println("change pin : " + code + " " + error);
                userInfo.setValue(null);
            }

            @Override
            public void onSuccess(String data) {
                pinDataResponse.setValue(data);
            }
        },chatID.getValue(),isPin);
    }
    public void deleteConversation(String conversationID) {
        System.out.println(" "+conversationID);
        OpenIMClient.getInstance().conversationManager.deleteConversation(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                Log.e("deleteConv Error", "onError: "+ error  +" "+ code);
            }

            @Override
            public void onSuccess(String data) {
                Log.d("deleteConv success", "onSuccess: "+data);
            }
        },conversationID);
    }

    public void burnChat(String conversionID, boolean isPrivate) {
        List<String> uidList = new ArrayList<>(); // 用户ID集合
        uidList.add(userID);
        OpenIMClient.getInstance().conversationManager.setOneConversationPrivateChat(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                oneConversationPrivateChat.setValue(null);
            }

            @Override
            public void onSuccess(String data) {
                oneConversationPrivateChat.setValue("done");
            }
        },conversionID, isPrivate );


    }
    public void addBlacklist() {
        OpenIMClient.getInstance().friendshipManager.addBlacklist(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                userInfo.setValue(null);
            }

            @Override
            public void onSuccess(String data) {
//                blackListData.setValue(data);
            }
        }, userID);
    }
    public void removeBlacklist() {
        OpenIMClient.getInstance().friendshipManager.removeBlacklist(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                userInfo.setValue(null);
            }

            @Override
            public void onSuccess(String data) {
//                blackListData.setValue(data);
            }
        }, userID);
    }
    public void DND(int status) {
        List<String> list = new LinkedList<>();
        list.add("single_"+userID);
        OpenIMClient.getInstance().conversationManager.setConversationRecvMessageOpt(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                userInfo.setValue(null);
                System.out.println("set DND error : " + code + " " + error);
            }

            @Override
            public void onSuccess(String data) {
//                blackListData.setValue(data);
                System.out.println("set DND : " + data);
            }
        }, list,status);
    }
    public void getDND() {
        List<String> list = new LinkedList<>();
        list.add("single_"+userID);
        OpenIMClient.getInstance().conversationManager.getConversationRecvMessageOpt(new OnBase<List<NotDisturbInfo>>() {
            @Override
            public void onError(int code, String error) {
                DNDResponse.setValue(null);
                System.out.println("getDND : " + code + " " + error);
            }

            @Override
            public void onSuccess(List<NotDisturbInfo> data) {
                DNDResponse.setValue(data);
                System.out.println("getDND : " + data);
            }
        }, list);
    }
    public void searchOneConversation(String userOrGroupID, long singleOrGroupChat) {
        OpenIMClient.getInstance().conversationManager.getOneConversation(new OnBase<ConversationInfo>() {
            @Override
            public void onError(int code, String error) {
                Log.e("searchOneConvo Error", "onError: "+ error  +" "+ code);
            }

            @Override
            public void onSuccess(ConversationInfo conversationInfo) {
                Log.d("searchOneConvo success", "onSuccess: "+conversationInfo);
                conversationInfoResponse.setValue(conversationInfo);
            }
        }, userOrGroupID, singleOrGroupChat);
    }
    public void deleteFriend() {
        OpenIMClient.getInstance().friendshipManager.deleteFriend(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                deleteFriend.setValue(null);
                Log.e("deleteFriend Error", "onError: "+ error  +" "+ code);
            }

            @Override
            public void onSuccess(String data) {
                deleteFriend.setValue("done");
                Log.d("deleteFriend success", "onSuccess: "+data);
            }
        },userID);
    }
    public void clearChat() {
        OpenIMClient.getInstance().messageManager.clearC2CHistoryMessageFromLocalAndSvr(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                clearChatHistoryResponse.setValue(null);
            }

            @Override
            public void onSuccess(String data) {
                clearChatHistoryResponse.setValue("done");
            }
        }, userID);
    }


    public interface ViewAction extends io.openim.android.ouicore.base.IView {

        void onError(String msg);

        void onSuccess(SearchResult o);
    }
}
