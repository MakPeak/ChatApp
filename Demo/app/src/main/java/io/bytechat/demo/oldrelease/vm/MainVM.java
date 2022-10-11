package io.bytechat.demo.oldrelease.vm;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import io.bytechat.demo.R;
import io.bytechat.demo.model.DiscoverResponse;
import io.bytechat.demo.model.LatestVersionData;
import io.bytechat.demo.model.LatestVersionResponse;
import io.bytechat.demo.oldrelease.repository.OpenIMService;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.im.IMEvent;
import io.openim.android.ouicore.net.RXRetrofit.N;
import io.openim.android.ouicore.net.RXRetrofit.NetObserver;
import io.openim.android.ouicore.net.RXRetrofit.Parameter;
import io.openim.android.ouicore.net.bage.Base;
import io.openim.android.ouicore.net.bage.GsonHel;
import io.openim.android.ouicore.utils.Constant;
import io.openim.android.ouicore.utils.L;
import io.openim.android.ouicore.widget.WaitDialog;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.listener.OnConnListener;
import io.openim.android.sdk.models.FriendApplicationInfo;
import io.openim.android.sdk.models.GroupApplicationInfo;
import io.openim.android.sdk.models.UserInfo;
import okhttp3.ResponseBody;

public class MainVM extends BaseViewModel<LoginVM.ViewAction> implements OnConnListener {

    public MutableLiveData<String> nickname = new MutableLiveData<>("");
    public MutableLiveData<String> avatar = new MutableLiveData<>("");
    public MutableLiveData<DiscoverResponse> discoverResponseMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<String> discoverURL = new MutableLiveData<>("");
    public MutableLiveData<List<GroupApplicationInfo>> groupApply = new MutableLiveData<>();
    public MutableLiveData<List<FriendApplicationInfo>> friendApply = new MutableLiveData<>();
    public MutableLiveData<Integer> counter = new MutableLiveData<>(0);
    public MutableLiveData<Integer> visibility = new MutableLiveData<>(View.INVISIBLE);
    private LoginCertificate loginCertificate;
    public MutableLiveData<LatestVersionData> latestVersionDataMutableLiveData = new MutableLiveData<>();

    public void getLatestVersion() {
        Parameter parameter = getParameter();
        WaitDialog waitDialog = showWait();
        N.API(OpenIMService.class).getLatestVersion(LoginCertificate.getCache(getContext()).token, parameter.buildJsonBody())
            .compose(N.IOMain())
            .subscribe(new NetObserver<LatestVersionResponse>(getContext()) {

                @Override
                public void onComplete() {
                    super.onComplete();
                    waitDialog.dismiss();
                }

                @Override
                public void onSuccess(LatestVersionResponse latestVersionResponse) {
                    waitDialog.dismiss();
                    latestVersionDataMutableLiveData.postValue(latestVersionResponse.getData());
                    System.out.println("LATEST VERSION API: " + latestVersionResponse.toString());
                }

                @Override
                public void onError(Throwable e) {
                    waitDialog.dismiss();
                    e.printStackTrace();
                }
            });
    }

    public void discover() {
        Parameter parameter = getParameter();
//        WaitDialog waitDialog = showWait();
        N.API(OpenIMService.class).discover(LoginCertificate.getCache(getContext()).token, parameter.buildJsonBody())
            .compose(N.IOMain())
            .subscribe(new NetObserver<DiscoverResponse>(getContext()) {

                @Override
                public void onComplete() {
                    super.onComplete();
//                    waitDialog.dismiss();
                }

                @Override
                public void onSuccess(DiscoverResponse discoverResponse) {
                    discoverResponseMutableLiveData.setValue(discoverResponse);
                    discoverURL.setValue(discoverResponse.getData().getUrl());
                    System.out.println("DISCOVER API: " + discoverResponse.toString());
                }

                @Override
                public void onError(Throwable e) {
                    IView.err(e.getMessage());
                }
            });
    }

    public void getRecvGroupApplicationList() {

        OpenIMClient.getInstance().groupManager.getRecvGroupApplicationList(new OnBase<List<GroupApplicationInfo>>() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onSuccess(List<GroupApplicationInfo> data) {
                if (!data.isEmpty()) {
                    groupApply.setValue(data);
                    for (int i = 0; i < data.size(); i++) {
                        if(data.get(i).getHandleResult() == 0){
                            counter.postValue(counter.getValue()+1);
                        }
                    }
                }
            }
        });
    }

    public void getRecvFriendApplicationList() {
        OpenIMClient.getInstance().friendshipManager.getRecvFriendApplicationList(new OnBase<List<FriendApplicationInfo>>() {
            @Override
            public void onError(int code, String error) {
                System.out.println(code + error);
            }

            @Override
            public void onSuccess(List<FriendApplicationInfo> data) {
                if (!data.isEmpty()) {
                    friendApply.setValue(data);
                    for (int i = 0; i < data.size(); i++) {
                        if(data.get(i).getHandleResult() == 0){
                            counter.postValue(counter.getValue()+1);
                        }
                    }
                }
            }
        });
    }

    @NonNull
    private Parameter getParameter() {
        return new Parameter()
            .add("operationID", "11")
            .add("client", "android");
    }

    @NonNull
    private WaitDialog showWait() {
        WaitDialog waitDialog = new WaitDialog(getContext());
        waitDialog.setNotDismiss();
        waitDialog.show();
        return waitDialog;
    }

    @Override
    protected void viewCreate() {
        IMEvent.getInstance().addConnListener(this);

        loginCertificate = LoginCertificate.getCache(getContext());
        if(loginCertificate != null) {
            authVM.token.setValue(loginCertificate.token);
        }

    }

    @Override
    protected void viewDestroy() {
        IMEvent.getInstance().removeConnListener(this);
    }

    @Override
    public void onConnectFailed(long code, String error) {

    }

    @Override
    public void onConnectSuccess() {
        visibility.setValue(View.VISIBLE);
    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onKickedOffline() {

    }

    @Override
    public void onUserTokenExpired() {

    }
}
