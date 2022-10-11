package io.openim.android.ouicontact.vm;


import static io.openim.android.ouicore.utils.Common.authViewModel;

import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import com.github.promeg.pinyinhelper.Pinyin;
import com.google.android.material.badge.BadgeDrawable;

import java.util.ArrayList;
import java.util.List;

import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.im.IMEvent;
import io.openim.android.ouicore.utils.Common;
import io.openim.android.ouicore.utils.L;
import io.openim.android.ouigroup.entity.ExUserInfo;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.listener.OnFriendshipListener;
import io.openim.android.sdk.listener.OnGroupListener;
import io.openim.android.sdk.models.BlacklistInfo;
import io.openim.android.sdk.models.ConversationInfo;
import io.openim.android.sdk.models.FriendApplicationInfo;
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.GroupApplicationInfo;
import io.openim.android.sdk.models.GroupInfo;
import io.openim.android.sdk.models.GroupMembersInfo;
import io.openim.android.sdk.models.UserInfo;

public class ContactVM extends BaseViewModel implements OnGroupListener {
    //群红点数量
    public MutableLiveData<Integer> groupDotNum = new MutableLiveData<>(0);
    //好友通知红点
//    public MutableLiveData<Integer> friendDotNum = new MutableLiveData<>(0);
    //申请列表
    public MutableLiveData<List<GroupApplicationInfo>> groupApply = new MutableLiveData<>();
    //好友申请列表
    public MutableLiveData<List<FriendApplicationInfo>> friendApply = new MutableLiveData<>();

    public MutableLiveData<List<FriendApplicationInfo>> friendSent = new MutableLiveData<>();

    public MutableLiveData<List<GroupApplicationInfo>> groupSent = new MutableLiveData<>();

    public MutableLiveData<List<GroupInfo>> groupCreatedInfo = new MutableLiveData<>();

    public MutableLiveData<List<GroupInfo>> groupJoinedInfo = new MutableLiveData<>();
    //申请详情
    public MutableLiveData<GroupApplicationInfo> groupDetail = new MutableLiveData<>();
    //好友申请详情
    public MutableLiveData<FriendApplicationInfo> friendDetail = new MutableLiveData<>();

    public MutableLiveData<List<String>> letters = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<List<ExUserInfo>> exUserInfo = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<List<FriendInfo>> friendsList = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<String> conversationID = new MutableLiveData<>();

    public MutableLiveData<List<UserInfo>> userInfo = new MutableLiveData<>();

    public static MutableLiveData<String> searchMyGroups = new MutableLiveData<>();

    @Override
    protected void viewCreate() {
        super.viewCreate();
        IMEvent.getInstance().addGroupListener(this);
//        IMEvent.getInstance().addFriendListener(this);
    }

    @Override
    protected void viewDestroy() {
        super.viewDestroy();
        IMEvent.getInstance().removeGroupListener(this);
//        IMEvent.getInstance().removeFriendListener(this);
    }

    //个人申请列表
    public void getRecvFriendApplicationList() {
        OpenIMClient.getInstance().friendshipManager.getRecvFriendApplicationList(new OnBase<List<FriendApplicationInfo>>() {
            @Override
            public void onError(int code, String error) {
                System.out.println(code + error);
            }

            @Override
            public void onSuccess(List<FriendApplicationInfo> data) {
                if (!data.isEmpty())
                    friendApply.setValue(data);

                System.out.println(data);
            }
        });
    }

    public void getSendFriendApplicationList() {
        OpenIMClient.getInstance().friendshipManager.getSendFriendApplicationList(new OnBase<List<FriendApplicationInfo>>() {
            @Override
            public void onError(int code, String error) {
                System.out.println(code + error);
            }

            @Override
            public void onSuccess(List<FriendApplicationInfo> data) {
                if (!data.isEmpty())
                    friendSent.setValue(data);

                System.out.println(data);
            }
        });
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

    public void getAllFriend() {
        OpenIMClient.getInstance().friendshipManager.getFriendList(new OnBase<List<UserInfo>>() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onSuccess(List<UserInfo> data) {
                if (data.isEmpty()) return;

                System.out.println("Friend List" + data.toString());
                ArrayList<FriendInfo> friendInfoList = new ArrayList<>();
                for (int i = 0; i < data.size(); i++){
                    friendInfoList.add(data.get(i).getFriendInfo());
                }
                friendsList.setValue(friendInfoList);
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
                        exInfos.add(exUserInfo);
                    }
                }
                for (ExUserInfo userInfo : exInfos) {
                    if (!letters.getValue().contains(userInfo.sortLetter))
                        letters.getValue().add(userInfo.sortLetter);
                }
                if (!otInfos.isEmpty())
                    letters.getValue().add("#");
                letters.setValue(letters.getValue());

                exUserInfo.getValue().addAll(exInfos);
                exUserInfo.getValue().addAll(otInfos);

                exUserInfo.setValue(exUserInfo.getValue());
            }
        });
    }

    //群申请列表
    public void getRecvGroupApplicationList() {

        OpenIMClient.getInstance().groupManager.getRecvGroupApplicationList(new OnBase<List<GroupApplicationInfo>>() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onSuccess(List<GroupApplicationInfo> data) {
                if (!data.isEmpty())
                    groupApply.setValue(data);

                System.out.println(data);
            }
        });
    }

    public void getSendGroupApplicationList() {

        OpenIMClient.getInstance().groupManager.getSendGroupApplicationList(new OnBase<List<GroupApplicationInfo>>() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onSuccess(List<GroupApplicationInfo> data) {
                if (!data.isEmpty())
                    groupSent.setValue(data);

                System.out.println(data);
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
                if (!data.isEmpty()) {
                    groupJoinedInfo.setValue(data);
                }
                System.out.println(data);
            }
        });
    }

    private OnBase onBase = new OnBase<String>() {
        @Override
        public void onError(int code, String error) {
            IView.toast(error);
        }

        @Override
        public void onSuccess(String data) {
            if (null != groupDetail)
                getRecvGroupApplicationList();
            if (null != friendDetail) {
                getRecvFriendApplicationList();
                getSendFriendApplicationList();
            }
            IView.onSuccess(null);
        }
    };

    //好友通过
    public void friendPass() {
        OpenIMClient.getInstance().friendshipManager.acceptFriendApplication(onBase, friendDetail.getValue().getFromUserID(), "");
    }

    public void friendPass(String fromUserID) {
        OpenIMClient.getInstance().friendshipManager.acceptFriendApplication(onBase, fromUserID, "");
        authViewModel.friendDotNum.setValue(authViewModel.friendDotNum.getValue() - 1);
        authViewModel.counterContact.setValue(authViewModel.counterContact.getValue() - 1);
    }

    //好友拒绝
    public void friendRefuse() {
        OpenIMClient.getInstance().friendshipManager.refuseFriendApplication(onBase, friendDetail.getValue().getFromUserID(), "");
    }

    public void friendRefuse(String fromUserID) {
        OpenIMClient.getInstance().friendshipManager.refuseFriendApplication(onBase, fromUserID, "");
        authViewModel.friendDotNum.setValue(authViewModel.friendDotNum.getValue() - 1);
        authViewModel.counterContact.setValue(authViewModel.counterContact.getValue() - 1);
    }

    //群通过
    public void pass() {
        OpenIMClient.getInstance().groupManager.acceptGroupApplication(onBase, groupDetail.getValue().getGroupID(), groupDetail.getValue().getUserID(), "");
        authViewModel.groupDotNum.setValue(authViewModel.groupDotNum.getValue() - 1);
        authViewModel.counterContact.setValue(authViewModel.counterContact.getValue() - 1);
    }

    public void pass(String groupID, String userID) {
        OpenIMClient.getInstance().groupManager.acceptGroupApplication(onBase, groupID, userID, "");
        authViewModel.groupDotNum.setValue(authViewModel.groupDotNum.getValue() - 1);
        authViewModel.counterContact.setValue(authViewModel.counterContact.getValue() - 1);
    }

    //群拒绝
    public void refuse() {
        OpenIMClient.getInstance().groupManager.acceptGroupApplication(onBase, groupDetail.getValue().getGroupID(), groupDetail.getValue().getUserID(), "");
        authViewModel.groupDotNum.setValue(authViewModel.groupDotNum.getValue() - 1);
        authViewModel.counterContact.setValue(authViewModel.counterContact.getValue() - 1);
    }

    public void refuse(String groupID, String userID) {
        OpenIMClient.getInstance().groupManager.acceptGroupApplication(onBase, groupID, userID, "");
        authViewModel.groupDotNum.setValue(authViewModel.groupDotNum.getValue() - 1);
        authViewModel.counterContact.setValue(authViewModel.counterContact.getValue() - 1);
    }

    @Override
    public void onGroupApplicationAccepted(GroupApplicationInfo info) {
        System.out.println("onGroupApplicationAccepted");
        groupDotNum.setValue(groupDotNum.getValue() - 1);
        authViewModel.counterContact.setValue(authViewModel.counterContact.getValue() - 1);
    }

    @Override
    public void onGroupApplicationAdded(GroupApplicationInfo info) {
//        groupDotNum.setValue(groupDotNum.getValue() + 1);
        System.out.println("onGroupApplicationAdded");
        authViewModel.groupDotNum.setValue(authViewModel.groupDotNum.getValue() + 1);
    }

    @Override
    public void onGroupApplicationDeleted(GroupApplicationInfo info) {

    }

    @Override
    public void onGroupApplicationRejected(GroupApplicationInfo info) {
        System.out.println("onGroupApplicationRejected");
        groupDotNum.setValue(groupDotNum.getValue() - 1);
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
        System.out.println("onJoinedGroupAdded");
//        authViewModel.groupDotNum.setValue(authViewModel.groupDotNum.getValue() + 1);
    }

    @Override
    public void onJoinedGroupDeleted(GroupInfo info) {

    }

//    @Override
//    public void onBlacklistAdded(BlacklistInfo u) {
//
//    }
//
//    @Override
//    public void onBlacklistDeleted(BlacklistInfo u) {
//
//    }
//
//    @Override
//    public void onFriendApplicationAccepted(FriendApplicationInfo u) {
////        friendDotNum.setValue(friendDotNum.getValue() - 1);
//    }
//
//    @Override
//    public void onFriendApplicationAdded(FriendApplicationInfo u) {
//        authViewModel.friendDotNum.setValue(authViewModel.friendDotNum.getValue() + 1);
//    }
//
//    @Override
//    public void onFriendApplicationDeleted(FriendApplicationInfo u) {
//
//    }
//
//    @Override
//    public void onFriendApplicationRejected(FriendApplicationInfo u) {
////        friendDotNum.postValue(friendDotNum.getValue() - 1);
//    }
//
//    @Override
//    public void onFriendInfoChanged(FriendInfo u) {
//
//    }
//
//    @Override
//    public void onFriendAdded(FriendInfo u) {
//
//    }
//
//    @Override
//    public void onFriendDeleted(FriendInfo u) {
//
//    }
}
