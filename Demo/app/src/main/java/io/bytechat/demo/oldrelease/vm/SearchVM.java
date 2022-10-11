package io.bytechat.demo.oldrelease.vm;

import android.util.Log;
import android.widget.Toast;

import androidx.collection.ArrayMap;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.bytechat.demo.R;
import io.bytechat.demo.ui.widget.AddFriendDialog;
import io.bytechat.demo.ui.widget.AddGroupDialog;
import io.bytechat.demo.ui.widget.VerificationGroupDialog;
import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.entity.MsgConversation;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.BaseImpl;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.listener.OnFriendshipListener;
import io.openim.android.sdk.listener.OnGroupListener;
import io.openim.android.sdk.models.BlacklistInfo;
import io.openim.android.sdk.models.ConversationInfo;
import io.openim.android.sdk.models.FriendApplicationInfo;
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.FriendshipInfo;
import io.openim.android.sdk.models.GroupApplicationInfo;
import io.openim.android.sdk.models.GroupInfo;
import io.openim.android.sdk.models.GroupMembersInfo;
import io.openim.android.sdk.models.Message;
import io.openim.android.sdk.models.SearchResult;
import io.openim.android.sdk.models.UserInfo;
import io.openim.android.sdk.utils.JsonUtil;
import io.openim.android.sdk.utils.ParamsUtil;

public class SearchVM extends BaseViewModel implements OnGroupListener, OnFriendshipListener {

    public MutableLiveData<List<GroupInfo>> groupsInfo = new MutableLiveData<>();
    public MutableLiveData<List<UserInfo>> userInfo = new MutableLiveData<>();
    public MutableLiveData<List<FriendshipInfo>> friendshipInfo = new MutableLiveData<>();
    public MutableLiveData<List<FriendInfo>> friendInfo = new MutableLiveData<>();
    public MutableLiveData<List<GroupInfo>> groupInfo = new MutableLiveData<>();
    public MutableLiveData<List<Message>> message = new MutableLiveData<>();
    public MutableLiveData<List<Message>> messageFile = new MutableLiveData<>();
    public MutableLiveData<String> conversationID = new MutableLiveData<>();
    public List<Message> messageArrayList = new ArrayList<>();
    public List<Message> messageFileArrayList = new ArrayList<>();

    public MutableLiveData<String> hail = new MutableLiveData<>();
    public MutableLiveData<String> remark = new MutableLiveData<>();
    //用户 或群组id
    public String searchContent = "";

    //y 搜索人 n 搜索群
    public boolean isPerson = false;


    public void searchPerson() {
        List<String> uidList = new ArrayList<>(); // 用户ID集合
        uidList.add(searchContent);
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
    public void searchPerson(AddFriendDialog addFriendDialog) {
        List<String> uidList = new ArrayList<>(); // 用户ID集合
        uidList.add(searchContent);
        OpenIMClient.getInstance().userInfoManager.getUsersInfo(new OnBase<List<UserInfo>>() {
            @Override
            public void onError(int code, String error) {
                userInfo.setValue(null);
            }

            @Override
            public void onSuccess(List<UserInfo> data) {
                addFriendDialog.finishLoading(data);

            }
        }, uidList);

    }

    public void checkFriend(List<UserInfo> data) {
        List<String> uIds = new ArrayList<>();
        uIds.add(data.get(0).getUserID());
        OpenIMClient.getInstance().friendshipManager.checkFriend(new OnBase<List<FriendshipInfo>>() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onSuccess(List<FriendshipInfo> data) {
                friendshipInfo.setValue(data);
            }
        }, uIds);
    }

    public void setFriendRemark(String userID, String remark) {
        OpenIMClient.getInstance().friendshipManager.setFriendRemark(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                System.out.println("SetFriendRemark-ERROR" + error);
            }

            @Override
            public void onSuccess(String data) {
                System.out.println("SetFriendRemark-SUCCESS" + data);
            }
        }, userID, remark);
    }

    public void addFriend() {
        OnBase<String> callBack = new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                Toast.makeText(getContext(), io.openim.android.ouicore.R.string.send_closed, Toast.LENGTH_SHORT).show();
                Log.e("add friend", " code : " + code + " error : " + error );
            }

            @Override
            public void onSuccess(String data) {
                Toast.makeText(getContext(), io.openim.android.ouicore.R.string.send_successfully, Toast.LENGTH_SHORT).show();
                hail.setValue(null);
            }
        };
        System.out.println("search content : " + searchContent + " hail: " + hail.getValue() + " isPerson :" + isPerson);
        if (isPerson)
            OpenIMClient.getInstance().friendshipManager.addFriend(callBack, searchContent, hail.getValue());
        else
            OpenIMClient.getInstance().groupManager.joinGroup(callBack, searchContent, hail.getValue(), 2);
    }
    public void addFriend(VerificationGroupDialog verificationGroupDialog) {
        OnBase<String> callBack = new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                Log.e("add friend", " code : " + code + " error : " + error );
            }

            @Override
            public void onSuccess(String data) {
                verificationGroupDialog.finishLoading();
            }
        };
        System.out.println("search content : " + searchContent + " hail: " + hail.getValue() + " isPerson :" + isPerson);
        if (isPerson)
            OpenIMClient.getInstance().friendshipManager.addFriend(callBack, searchContent, hail.getValue());
        else
            OpenIMClient.getInstance().groupManager.joinGroup(callBack, searchContent, hail.getValue(), 2);
    }
    public void search() {
        if (isPerson)
            searchPerson();
        else
            getGroupInfo();
    }

    public void getGroupInfo() {
        List<String> groupIds = new ArrayList<>(); // 群ID集合
        groupIds.add(searchContent);
        OpenIMClient.getInstance().groupManager.getGroupsInfo(new OnBase<List<GroupInfo>>() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onSuccess(List<GroupInfo> data) {
                groupsInfo.setValue(data);
            }
        }, groupIds);
    }
    public void getGroupInfo(AddGroupDialog dialog) {
        List<String> groupIds = new ArrayList<>(); // 群ID集合
        groupIds.add(searchContent);
        OpenIMClient.getInstance().groupManager.getGroupsInfo(new OnBase<List<GroupInfo>>() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onSuccess(List<GroupInfo> data) {
                getGroupMemberList(dialog , data);
            }
        }, groupIds);
    }
    public void getGroupMemberList(AddGroupDialog dialog , List<GroupInfo> groupInfo) {
        List<String> groupIds = new ArrayList<>(); // 群ID集合
        groupIds.add(searchContent);
        OpenIMClient.getInstance().groupManager.getGroupMemberList(new OnBase<List<GroupMembersInfo>>() {
            @Override
            public void onError(int code, String error) {
                System.out.println("code : " + code + "error : "+ error);
                dialog.finishLoading(groupInfo , null);

            }

            @Override
            public void onSuccess(List<GroupMembersInfo> data) {
                System.out.println("getGroupMemberList It's done successfully ");
                dialog.finishLoading(groupInfo , data);
            }
        }, groupIds.get(0),0,0,10000);
    }

    public void searchContacts(String search) {
        List<String> stringList = new ArrayList<>();
        stringList.add(search);
        OpenIMClient.getInstance().friendshipManager.searchFriends(new OnBase<List<FriendInfo>>() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onSuccess(List<FriendInfo> data) {
                friendInfo.setValue(data);
                System.out.println("FRIENDS LIST" + data.toString());
            }
        }, stringList, true, true, false);
    }

    public void searchGroup(String search) {
        List<String> stringList = new ArrayList<>();
        stringList.add(search);
        OpenIMClient.getInstance().groupManager.searchGroups(new OnBase<List<GroupInfo>>() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onSuccess(List<GroupInfo> data) {
                groupInfo.setValue(data);
                System.out.println("GROUPS LIST" + data.toString());
            }
        }, stringList, true, true);
    }

    public void searchChat(String search, int type){
        List<Integer> typeList = new LinkedList<>();
        List<String> keywordList = new LinkedList<>();
        keywordList.add(search);
        typeList.add(type);
        OpenIMClient.getInstance().messageManager.searchLocalMessages(new OnBase<SearchResult>() {
            @Override
            public void onError(int code, String error) {
                Log.e("Search chat : " , "code : " + code + " error : "+ error);
                IView.onError(error);
            }

            @Override
            public void onSuccess(SearchResult data) {
                if(data.getSearchResultItems() == null) {
                    messageArrayList.clear();
                    message.setValue(messageArrayList);
                    return;
                }
                messageArrayList.clear();
                for(int i = 0; i < data.getSearchResultItems().size(); i++){
                    messageArrayList.add(data.getSearchResultItems().get(i).getMessageList().get(0));
                }
                message.setValue(messageArrayList);
                IView.onSuccess(data);
                System.out.println("CHAT: " + messageArrayList.toString());
            }

        }, null, keywordList, 2, null, typeList, 0, 0, 1, 10000);
    }

    public void searchFile(String search, int type){
        List<Integer> typeList = new LinkedList<>();
        List<String> keywordList = new LinkedList<>();
        keywordList.add(search);
        typeList.add(type);
        OpenIMClient.getInstance().messageManager.searchLocalMessages(new OnBase<SearchResult>() {
            @Override
            public void onError(int code, String error) {
                Log.e("Search chat : " , "code : " + code + " error : "+ error);
                IView.onError(error);
            }

            @Override
            public void onSuccess(SearchResult data) {
                if(data.getSearchResultItems() == null) {
                    messageFileArrayList.clear();
                    messageFile.setValue(messageFileArrayList);
                    return;
                }
                messageFileArrayList.clear();
                for(int i = 0; i < data.getSearchResultItems().size(); i++){
                    messageFileArrayList.add(data.getSearchResultItems().get(i).getMessageList().get(0));
                }
                messageFile.setValue(messageFileArrayList);
                IView.onSuccess(data);
                System.out.println("FILE: " + messageArrayList.toString());
            }

        }, null, keywordList, 2, null, typeList, 0, 0, 1, 10000);
    }

    public void searchOneConversation(String userOrGroupID, long singleOrGroupChat) {
        OpenIMClient.getInstance().conversationManager.getOneConversation(new OnBase<ConversationInfo>() {
            @Override
            public void onError(int code, String error) {
                Log.e("searchOneConvo Error", "onError: "+ error  +" "+ code);
            }

            @Override
            public void onSuccess(ConversationInfo data) {
                Log.d("searchOneConvo success", "onSuccess: "+data);
                conversationID.setValue(data.getConversationID());
            }
        }, userOrGroupID, singleOrGroupChat);
    }

    @Override
    public void onBlacklistAdded(BlacklistInfo u) {

    }

    @Override
    public void onBlacklistDeleted(BlacklistInfo u) {

    }

    @Override
    public void onFriendApplicationAccepted(FriendApplicationInfo u) {

    }

    @Override
    public void onFriendApplicationAdded(FriendApplicationInfo u) {

    }

    @Override
    public void onFriendApplicationDeleted(FriendApplicationInfo u) {

    }

    @Override
    public void onFriendApplicationRejected(FriendApplicationInfo u) {

    }

    @Override
    public void onFriendInfoChanged(FriendInfo u) {

    }

    @Override
    public void onFriendAdded(FriendInfo u) {

    }

    @Override
    public void onFriendDeleted(FriendInfo u) {

    }

    @Override
    public void onGroupApplicationAccepted(GroupApplicationInfo info) {

    }

    @Override
    public void onGroupApplicationAdded(GroupApplicationInfo info) {

    }

    @Override
    public void onGroupApplicationDeleted(GroupApplicationInfo info) {

    }

    @Override
    public void onGroupApplicationRejected(GroupApplicationInfo info) {

    }

    @Override
    public void onGroupInfoChanged(GroupInfo info) {

    }

    @Override
    public void onGroupMemberAdded(GroupMembersInfo info) {

    }

    @Override
    public void onGroupMemberDeleted(GroupMembersInfo info) {

    }

    @Override
    public void onGroupMemberInfoChanged(GroupMembersInfo info) {

    }

    @Override
    public void onJoinedGroupAdded(GroupInfo info) {

    }

    @Override
    public void onJoinedGroupDeleted(GroupInfo info) {

    }
}
