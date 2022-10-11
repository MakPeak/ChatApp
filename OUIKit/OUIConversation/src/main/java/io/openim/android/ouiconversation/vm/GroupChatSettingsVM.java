package io.openim.android.ouiconversation.vm;

import static io.openim.android.ouicore.net.RXRetrofit.N.mRetrofit;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.widget.WaitDialog;
import io.openim.android.ouigroup.service.OpenIMService;
import io.openim.android.ouigroup.service.Root;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.models.ConversationInfo;
import io.openim.android.sdk.models.FriendshipInfo;
import io.openim.android.sdk.models.GroupInfo;
import io.openim.android.sdk.models.GroupInviteResult;
import io.openim.android.sdk.models.GroupMembersInfo;
import io.openim.android.sdk.models.NotDisturbInfo;
import io.openim.android.sdk.models.UserInfo;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupChatSettingsVM extends BaseViewModel {
    private final String TAG = "GroupChatSettingsVM";
    public MutableLiveData<List<GroupMembersInfo>> groupMembersInfo = new MutableLiveData<>();
    public MutableLiveData<List<GroupInfo>> groupsInfo = new MutableLiveData<>();
    public MutableLiveData<String> makeAdminRoleMsg = new MutableLiveData<>();
    public MutableLiveData<String> removeAdminRoleMsg = new MutableLiveData<>();
    public MutableLiveData<String> modifyRoleLevelMsg = new MutableLiveData<>();
    public MutableLiveData<String> removeStatusMsg = new MutableLiveData<>();
    public MutableLiveData<String> muteMemberStatusMsg = new MutableLiveData<>();
    public MutableLiveData<Long> mutedForSeconds=new MutableLiveData<>();
    public MutableLiveData<String> changeNickNameStatusMsg = new MutableLiveData<>();
    public MutableLiveData<String> modifyGroupInfoStatusMsg = new MutableLiveData<>();
    public MutableLiveData<String> pinGroupResponse = new MutableLiveData<>();
    public MutableLiveData<List<NotDisturbInfo>> DNDResponse = new MutableLiveData<>();
    public MutableLiveData<String> clearChatHistoryResponse = new MutableLiveData<>();
    public MutableLiveData<String> exitGroupResponse=new MutableLiveData<>();
    public MutableLiveData<String> dismissGroupResponse=new MutableLiveData<>();
    public String searchContent="";
    public MutableLiveData<List<UserInfo>> userInfo = new MutableLiveData<>();
    public MutableLiveData<List<FriendshipInfo>> friendshipInfo = new MutableLiveData<>();
    public MutableLiveData<String> allBanResponse=new MutableLiveData<>();
    public MutableLiveData<Uri> imageURI = new MutableLiveData<>();
    public MutableLiveData<String> avatar = new MutableLiveData<>();
    public MutableLiveData<String> faceURL = new MutableLiveData<>("");
    public MutableLiveData<ConversationInfo> conversationInfoResponse = new MutableLiveData<>();

    @NonNull
    private WaitDialog showWait() {
        WaitDialog waitDialog = new WaitDialog(getContext());
        waitDialog.setNotDismiss();
        waitDialog.show();
        return waitDialog;
    }

    public interface ViewActionPicture extends io.openim.android.ouicore.base.IView {
        void finishUploadingFile();
        void errorUploadingFile();
    }

    public void uploadFile(ViewActionPicture Iview2, Context context){
        WaitDialog waitDialog = showWait();
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), new File(imageURI.getValue().getPath()));
        System.out.println(imageURI.getValue().getPath());
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageURI.getValue().getPath(), requestFile);

        OpenIMService service = mRetrofit.create(OpenIMService.class);
        Call<Root> call = service.uploadFile(LoginCertificate.getCache(context).token, body, 3, ""+System.currentTimeMillis());
        call.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                System.out.println("1111111");
                waitDialog.dismiss();
                if (response.code() == 200){
                    System.out.println("3333333");
                    Iview2.finishUploadingFile();
                    faceURL.postValue(response.body().data.uRL); ;
                } else {
                    Iview2.errorUploadingFile();
                }

            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                System.out.println("2222222" + t.getMessage());
                waitDialog.dismiss();
                Iview2.errorUploadingFile();

            }
        });
    }

    public void searchOneConversation(String userOrGroupID, long singleOrGroupChat) {
        WaitDialog waitDialog = showWait();
        OpenIMClient.getInstance().conversationManager.getOneConversation(new OnBase<ConversationInfo>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();

                Log.e(TAG, "searchOneConversation onError: "+ error  +" "+ code);
                conversationInfoResponse.setValue(null);
            }

            @Override
            public void onSuccess(ConversationInfo conversationInfo) {
                waitDialog.dismiss();

                Log.d(TAG, "searchOneConversation onSuccess: "+conversationInfo);
                conversationInfoResponse.setValue(conversationInfo);
            }
        }, userOrGroupID, singleOrGroupChat);
    }

    //Get Group Information
    public void getGroupInfo(String groupID) {
        WaitDialog waitDialog = showWait();
        List<String> groupIds = new ArrayList<>(); // 群ID集合
        groupIds.add(groupID);
        OpenIMClient.getInstance().groupManager.getGroupsInfo(new OnBase<List<GroupInfo>>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
            }

            @Override
            public void onSuccess(List<GroupInfo> data) {
                waitDialog.dismiss();
                Log.d(TAG, "onSuccess: data" + data.toString());
                groupsInfo.setValue(data);
                //getGroupMemberList(data);
            }
        }, groupIds);
    }

    //Modify Group Information
    public void modifyGroupInfo(String groupID, String groupName, String faceURL, String notification, String introduction, String ex){
        WaitDialog waitDialog = showWait();

        OpenIMClient.getInstance().groupManager.setGroupInfo(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
                Log.d(TAG, "onError: "+code + " "+ error);
                modifyGroupInfoStatusMsg.setValue(error);
            }

            @Override
            public void onSuccess(String data) {
                waitDialog.dismiss();
                Log.d(TAG, "onSuccess: data -> "+data);
                modifyGroupInfoStatusMsg.setValue(data);

            }
        },groupID, groupName, faceURL, notification, introduction, ex);
    }

    //Used in MemberDetailsActivity for getting member information
    public void searchPerson() {
        WaitDialog waitDialog = showWait();
        List<String> uidList = new ArrayList<>(); // 用户ID集合
        uidList.add(searchContent);
        OpenIMClient.getInstance().userInfoManager.getUsersInfo(new OnBase<List<UserInfo>>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();userInfo.setValue(null);
            }

            @Override
            public void onSuccess(List<UserInfo> data) {
                waitDialog.dismiss();userInfo.setValue(data);
            }
        }, uidList);

    }

    public void checkFriend(List<UserInfo> data) {
        WaitDialog waitDialog = showWait();
        List<String> uIds = new ArrayList<>();
        uIds.add(data.get(0).getUserID());
        OpenIMClient.getInstance().friendshipManager.checkFriend(new OnBase<List<FriendshipInfo>>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
            }

            @Override
            public void onSuccess(List<FriendshipInfo> data) {
                waitDialog.dismiss();friendshipInfo.setValue(data);
            }
        }, uIds);
    }

    public void setGroupNickName(String groupId, String userId, String groupNickName) {
        WaitDialog waitDialog = showWait();

        OpenIMClient.getInstance().groupManager.setGroupMemberNickname(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();changeNickNameStatusMsg.setValue(error);
            }

            @Override
            public void onSuccess(String data) {
                Log.d(TAG, "onSuccess: setGroupNickName" + data);
                waitDialog.dismiss();
                changeNickNameStatusMsg.setValue(data);
            }}, groupId, userId, groupNickName);

    }

    public void getGroupMemberList(String groupID) {
        WaitDialog waitDialog = showWait();
        OpenIMClient.getInstance().groupManager.getGroupMemberList(new OnBase<List<GroupMembersInfo>>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
                System.out.println("code : " + code + "error : " + error);
            }

            @Override
            public void onSuccess(List<GroupMembersInfo> data) {
                waitDialog.dismiss();
                System.out.println("getGroupMemberList It's done successfully ");
                groupMembersInfo.setValue(data);
            }
        }, groupID, 0, 0, 10000);
    }

    //View Group Members - 1. Mute
    public void muteMember(String gid, String uid, long seconds) {
        WaitDialog waitDialog = showWait();
        mutedForSeconds.setValue(seconds);

        OpenIMClient.getInstance().groupManager.changeGroupMemberMute(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
                Log.d(TAG, "muteMember onError: " + gid + " " + uid + " " + seconds);
                Log.d(TAG, "muteMember onError: " + code);
                Log.d(TAG, "muteMember onError: " + error);
                muteMemberStatusMsg.setValue(error);
            }

            @Override
            public void onSuccess(String data) {
                waitDialog.dismiss();
                Log.d(TAG, "muteMember onSuccess: " + data);
                if(seconds==0)
                    muteMemberStatusMsg.setValue("0");
                else
                    muteMemberStatusMsg.setValue("1");
               // getGroupMemberList(gid);
            }
        }, gid, uid, seconds);
    }

    // Owner View View Group Members - 1. Make Admin
    public void makeAdmin(String groupID, String userID, long roleLevel){
        WaitDialog waitDialog = showWait();

        OpenIMClient.getInstance().groupManager.setGroupMemberRoleLevel(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
                Log.d(TAG, "makeAdmin onError: " + code);
                Log.d(TAG, "makeAdmin onError: " + error);
                makeAdminRoleMsg.setValue(error);
            }

            @Override
            public void onSuccess(String data) {
                waitDialog.dismiss();
                Log.d(TAG, "makeAdmin onSuccess:groupID " + groupID);
                Log.d(TAG, "makeAdmin onSuccess:userID " + userID);
                Log.d(TAG, "makeAdmin onSuccess:roleLevel " + roleLevel);
                Log.d(TAG, "makeAdmin onSuccess: data received -> " + data);
                makeAdminRoleMsg.setValue(data);
            }
        }, groupID, userID, roleLevel);
       // transferOwner(groupID, userID, roleLevel);
    }
    public void removeAdmin(String groupID, String userID, long roleLevel){
        WaitDialog waitDialog = showWait();
        OpenIMClient.getInstance().groupManager.setGroupMemberRoleLevel(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
                Log.d(TAG, "removeAdmin onError: " + code);
                Log.d(TAG, "removeAdmin onError: " + error);
                removeAdminRoleMsg.setValue(error);
            }

            @Override
            public void onSuccess(String data) {
                waitDialog.dismiss();
                Log.d(TAG, "removeAdmin onSuccess:groupID " + groupID);
                Log.d(TAG, "removeAdmin onSuccess:userID " + userID);
                Log.d(TAG, "removeAdmin onSuccess:roleLevel " + roleLevel);
                Log.d(TAG, "removeAdmin onSuccess: data received -> " + data);
                removeAdminRoleMsg.setValue(data);
            }
        }, groupID, userID, roleLevel);
       // transferOwner(groupID, userID, roleLevel);
    }

    //View Group Members - 2. For transferring the ownership
    public void transferOwner(String groupID, String userID) {
        WaitDialog waitDialog = showWait();

        OpenIMClient.getInstance().groupManager.transferGroupOwner(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
                Log.d(TAG, "transferOwner onError:groupID " + groupID);
                Log.d(TAG, "transferOwner onError:userID " + userID);
                Log.d(TAG, "transferOwner onError: " + code);
                Log.d(TAG, "transferOwner onError: " + error);
                modifyRoleLevelMsg.setValue(error);
            }

            @Override
            public void onSuccess(String data) {
                waitDialog.dismiss();
                Log.d(TAG, "transferOwner onSuccess:groupID " + groupID);
                Log.d(TAG, "transferOwner onSuccess:userID " + userID);
                Log.d(TAG, "transferOwner onSuccess: data received -> " + data);
                modifyRoleLevelMsg.setValue(data);
            }
        }, groupID, userID);
    }

    //View Group Members - 3. Remove Member
    public void removeMember(String groupId, String userId, String reason) {
        WaitDialog waitDialog = showWait();

        List<String> uidList = new ArrayList<>();
        uidList.add(userId);

        OpenIMClient.getInstance().groupManager.kickGroupMember(new OnBase<List<GroupInviteResult>>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
                Log.d(TAG, "removeMember onError:groupId " + groupId);
                Log.d(TAG, "removeMember onError: userId " + userId);
                Log.d(TAG, "removeMember onError: reason " + reason);
                Log.d(TAG, "removeMember onError: code " + code);
                Log.d(TAG, "removeMember onError: " + error);
                removeStatusMsg.setValue(error);
            }

            @Override
            public void onSuccess(List<GroupInviteResult> data) {
                waitDialog.dismiss();
                Log.d(TAG, "removeMember onSuccess: " + data.toString());
                removeStatusMsg.setValue("Done");
            }
        }, groupId, uidList, reason);
    }

    //**** NOTE: For all calls using conversationManager, pass the group Id full including group_.

    public void changePin(boolean isPin, String groupID) {
        WaitDialog waitDialog = showWait();

        OpenIMClient.getInstance().conversationManager.pinConversation(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
                Log.d(TAG, "onError: groupid->" +groupID+"isPin:"+isPin);
                Log.d(TAG, "onError: changePin error code -> " + code + " " + error);
            }

            @Override
            public void onSuccess(String data) {
                waitDialog.dismiss();
                Log.d(TAG, "onSuccess: changePin data -> "+data);
                pinGroupResponse.setValue(data);
            }
        },groupID,isPin);
    }

    public void allBan(String gid, boolean mute){
        WaitDialog waitDialog = showWait();

        OpenIMClient.getInstance().groupManager.changeGroupMute(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
                Log.d(TAG, "onError: allBan "+error);
                allBanResponse.setValue(null);
            }

            @Override
            public void onSuccess(String data) {
                waitDialog.dismiss();
                Log.d(TAG, "onSuccess: allBan "+data);
                allBanResponse.setValue(data);
            }
        },gid, mute );
    }

    public void DND(int status, String groupID) {
        WaitDialog waitDialog = showWait();

        List<String> list = new LinkedList<>();
        list.add(groupID);
        OpenIMClient.getInstance().conversationManager.setConversationRecvMessageOpt(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
                Log.d(TAG, "onError: DND error : " + code + " " + error);
            }

            @Override
            public void onSuccess(String data) {
                waitDialog.dismiss();
                Log.d(TAG, "onSuccess: DND data:"+data);
//                blackListData.setValue(data);
            }
        }, list,status);
    }

    public void getDND(String groupID) {
        WaitDialog waitDialog = showWait();

        List<String> list = new LinkedList<>();
        list.add(groupID);
        OpenIMClient.getInstance().conversationManager.getConversationRecvMessageOpt(new OnBase<List<NotDisturbInfo>>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
                Log.d(TAG, "onError: getDND : " + code + " " + error);
                DNDResponse.setValue(null);
            }

            @Override
            public void onSuccess(List<NotDisturbInfo> data) {
                waitDialog.dismiss();
                Log.d(TAG, "onSuccess: getDND : " + data);
                DNDResponse.setValue(data);
            }
        }, list);
    }

    public void clearChatHistory(String groupID){
        WaitDialog waitDialog = showWait();

        OpenIMClient.getInstance().messageManager.clearGroupHistoryMessageFromLocalAndSvr(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();clearChatHistoryResponse.setValue(null);
            }

            @Override
            public void onSuccess(String data) {
                waitDialog.dismiss();clearChatHistoryResponse.setValue("done");
            }
        }, groupID);
    }

    public void dismissGroup(String groupId){
        WaitDialog waitDialog = showWait();

        OpenIMClient.getInstance().groupManager.dismissGroup(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
                Log.d(TAG, "onError: dismissGroup "+error);
                dismissGroupResponse.setValue(null);
            }

            @Override
            public void onSuccess(String data) {
                waitDialog.dismiss();
                Log.d(TAG, "onSuccess: dismissGroup "+data);
                dismissGroupResponse.setValue(data);
            }
        }, groupId);
    }
    public void exitGroup(String groupId){
        WaitDialog waitDialog = showWait();

        OpenIMClient.getInstance().groupManager.quitGroup(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
                Log.d(TAG, "onError: exitGroup "+error);
                exitGroupResponse.setValue(null);
            }

            @Override
            public void onSuccess(String data) {
                waitDialog.dismiss();
                Log.d(TAG, "onSuccess: exitGroup "+data);
                exitGroupResponse.setValue(data);
            }
        }, groupId);
    }
}
