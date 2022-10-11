package io.bytechat.demo.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.Timer;

import io.bytechat.demo.oldrelease.repository.OpenIMService;
import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.base.IView;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.net.RXRetrofit.N;
import io.openim.android.ouicore.net.RXRetrofit.NetObserver;
import io.openim.android.ouicore.net.RXRetrofit.Parameter;
import io.openim.android.ouicore.net.bage.Base;
import io.openim.android.ouicore.net.bage.GsonHel;
import io.openim.android.ouicore.widget.WaitDialog;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnBase;
import okhttp3.ResponseBody;

public class SetPasswordViewModel extends BaseViewModel<SetPasswordViewModel.ViewAction> {

    public static final int MAX_COUNTDOWN = 10;

    public MutableLiveData<String> pwd = new MutableLiveData<>("");
    public MutableLiveData<String> rePwd = new MutableLiveData<>("");
    public MutableLiveData<String> phoneNumber = new MutableLiveData<>("");
    public MutableLiveData<String> verificationCode = new MutableLiveData<>("");

    public void register(){
        Parameter parameter = getParameter();
        WaitDialog waitDialog = showWait();
        N.API(OpenIMService.class).register(parameter.buildJsonBody())
            .compose(N.IOMain())
            .subscribe(new NetObserver<ResponseBody>(context.get()) {
                @Override
                public void onComplete() {
                    super.onComplete();
                    waitDialog.dismiss();
                }

                @Override
                public void onSuccess(ResponseBody o) {
                    String body = null;
                    try {
                        body = o.string();
                        Base<LoginCertificate> data = GsonHel.dataObject(body, LoginCertificate.class);
                        if(data.errCode != 0){
                            IView.onErr(data.errMsg);
                            return;
                        }
                        data.data.cache(getContext());
                        IView.onSucce(data.data);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void onFailure(Throwable e) {
                    IView.onErr(e.getMessage());
                }
            });
    }

    public void reSetPassword(){
        Parameter parameter = getParameterResetPassword();
        WaitDialog waitDialog = showWait();
        N.API(OpenIMService.class).reSetPassword(parameter.buildJsonBody())
            .compose(N.IOMain())
            .subscribe(new NetObserver<ResponseBody>(context.get()) {
                @Override
                public void onComplete() {
                    super.onComplete();
                    waitDialog.dismiss();
                }

                @Override
                public void onSuccess(ResponseBody o) {
                    String body = null;
                    try {
                        body = o.string();
                        Base<LoginCertificate> data = GsonHel.dataObject(body, LoginCertificate.class);
                        if(data.errCode != 0){
                            IView.onErr(data.errMsg);
                            return;
                        }
                        IView.onSucce(data.data);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void onFailure(Throwable e) {
                    IView.onErr(e.getMessage());
                }
            });
    }

    @NonNull
    private Parameter getParameter() {
        Parameter parameter = new Parameter()
            .add("phoneNumber", phoneNumber.getValue())
            .add("password", pwd.getValue())
            .add("verificationCode", verificationCode.getValue())
            .add("platform", 2)
            .add("operationID", System.currentTimeMillis() + "");
        return parameter;
    }
    @NonNull
    private Parameter getParameterResetPassword() {
        Parameter parameter = new Parameter()
            .add("phoneNumber", phoneNumber.getValue())
            .add("newPassword", pwd.getValue())
            .add("verificationCode", verificationCode.getValue())
            .add("platform", 2)
            .add("operationID", System.currentTimeMillis() + "");
        return parameter;
    }


    @NonNull
    private WaitDialog showWait() {
        WaitDialog waitDialog = new WaitDialog(getContext());
        waitDialog.setNotDismiss();
        waitDialog.show();
        return waitDialog;
    }

    private Timer timer;

    @Override
    protected void viewDestroy() {
        super.viewDestroy();
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
    }

    public interface ViewAction extends io.openim.android.ouicore.base.IView {

        void onErr(String msg);

        void onSucce(LoginCertificate o);
    }

}
