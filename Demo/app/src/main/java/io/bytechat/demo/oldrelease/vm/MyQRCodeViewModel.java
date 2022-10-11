package io.bytechat.demo.oldrelease.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.base.IView;
import io.openim.android.ouicore.utils.L;
import io.openim.android.ouicore.widget.WaitDialog;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.models.UserInfo;

public class MyQRCodeViewModel extends BaseViewModel {

    public MutableLiveData<String> qrCode = new MutableLiveData<>("");
    public MutableLiveData<String> nickname = new MutableLiveData<>("");
    public MutableLiveData<String> userID = new MutableLiveData<>("");
    public MutableLiveData<String> avatar = new MutableLiveData<>("");

    public void getProfileData(){
        WaitDialog showWait = showWait() ;
        OpenIMClient.getInstance().userInfoManager.getSelfUserInfo(new OnBase<UserInfo>() {
            @Override
            public void onError(int code, String error) {
                L.e("error :"+code);
                showWait.dismiss();
            }

            @Override
            public void onSuccess(UserInfo data) {
                // 返回当前登录用户的资料
                showWait.dismiss();
                nickname.setValue(data.getNickname());
                userID.setValue(data.getUserID());
                avatar.setValue(data.getFaceURL());
            }
        });
    }
    @NonNull
    private WaitDialog showWait() {
        WaitDialog waitDialog = new WaitDialog(getContext());
        waitDialog.setNotDismiss();
        waitDialog.show();
        return waitDialog;
    }
    public interface ViewAction extends IView {

        void onErr(String msg);

        void onSucc(Object o);

    }
}
