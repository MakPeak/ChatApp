package io.openim.android.ouigroup.vm;

import static io.openim.android.ouicore.net.RXRetrofit.N.mRetrofit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.github.promeg.pinyinhelper.Pinyin;

import java.io.File;
import java.io.FileOutputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.base.IView;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.utils.Common;
import io.openim.android.ouicore.utils.L;
import io.openim.android.ouicore.widget.WaitDialog;
import io.openim.android.ouigroup.R;
import io.openim.android.ouigroup.entity.ExUserInfo;
import io.openim.android.ouigroup.service.OpenIMService;
import io.openim.android.ouigroup.service.Root;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.GroupInfo;
import io.openim.android.sdk.models.GroupInviteResult;
import io.openim.android.sdk.models.GroupMemberRole;
import io.openim.android.sdk.models.GroupMembersInfo;
import io.openim.android.sdk.models.UserInfo;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupVM extends BaseViewModel {
    public MutableLiveData<String> groupName = new MutableLiveData<>("");
    public MutableLiveData<String> groupDisc = new MutableLiveData<>("");
    public MutableLiveData<String> faceURL = new MutableLiveData<>("");
    public MutableLiveData<GroupInfo> groupsInfo = new MutableLiveData<>();
    public MutableLiveData<List<ExUserInfo>> exUserInfo = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Boolean> groupInviteResult = new MutableLiveData<>();
    public String groupId;
    public MutableLiveData<List<String>> letters = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<List<FriendInfo>> selectedFriendInfo = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Uri> imageURI = new MutableLiveData<>();
    public MutableLiveData<String> avatar = new MutableLiveData<>();


    @Override
    protected void viewCreate() {
        super.viewCreate();
    }

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

    /**
     * 获取群组信息
     */
    public void getGroupsInfo() {
        List<String> groupIds = new ArrayList<>(); // 群ID集合
        groupIds.add(groupId);
        OpenIMClient.getInstance().groupManager.getGroupsInfo(new OnBase<List<GroupInfo>>() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onSuccess(List<GroupInfo> data) {
                if (!data.isEmpty())
                    groupsInfo.setValue(data.get(0));
            }
        }, groupIds);
    }

    public void inviteUsers(List<String> uidList) {
        WaitDialog waitDialog = showWait();
        System.out.println("groupId :"+ groupId + " " + uidList.size());
        OpenIMClient.getInstance().groupManager.inviteUserToGroup(new OnBase<List<GroupInviteResult>>() {
            @Override
            public void onError(int code, String error) {
                waitDialog.dismiss();
                Log.e("inviteUserToGroup" , "code : " + code + " error : "+ error);
            }

            @Override
            public void onSuccess(List<GroupInviteResult> data) {
                waitDialog.dismiss();
                Log.e("inviteUserToGroup" , "onSuccess : " + data);
                groupInviteResult.setValue(true);
            }
        }, groupId ,uidList ,"");
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
                    faceURL.setValue(response.body().data.uRL) ;
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

    /**
     * 创建群组
     */
    public void createGroup() {
        WaitDialog waitDialog = showWait();
        List<GroupMemberRole> groupMemberRoles = new ArrayList<>();
        LoginCertificate loginCertificate = LoginCertificate.getCache(getContext());
        for (FriendInfo friendInfo : selectedFriendInfo.getValue()) {
            GroupMemberRole groupMemberRole = new GroupMemberRole();
            if (friendInfo.getUserID().equals(loginCertificate.userID)) {
                groupMemberRole.setRoleLevel(2);
            } else
                groupMemberRole.setRoleLevel(1);
            groupMemberRole.setUserID(friendInfo.getUserID());
            groupMemberRoles.add(groupMemberRole);
        }
        OpenIMClient.getInstance().groupManager.createGroup(new OnBase<GroupInfo>() {

            @Override
            public void onError(int code, String error) {
                System.out.println("3454353");
                IView.onError(error);
                waitDialog.dismiss();
            }

            @Override
            public void onSuccess(GroupInfo data) {
                waitDialog.dismiss();
                IView.onSuccess(data);
            }
        }, groupName.getValue(), faceURL.getValue(), null, groupDisc.getValue(), 0, null, groupMemberRoles);
    }

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

}
