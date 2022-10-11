package io.bytechat.demo.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.HashMap;
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

public class OTPViewModel extends BaseViewModel<OTPViewModel.ViewAction> {
    public static final int MAX_COUNTDOWN = 10;

    public MutableLiveData<String> verificationCode = new MutableLiveData<>("");
    public MutableLiveData<String> phoneNumber = new MutableLiveData<>("");
    public MutableLiveData<Integer> usedforvalue = new MutableLiveData<Integer>(1);




    @NonNull
    private Parameter getParameter(int flowType) {
        System.out.println("usedforvalue.getValue()" + usedforvalue.getValue());
        Parameter parameter = new Parameter()
            .add("phoneNumber",  phoneNumber.getValue())
            .add("email", "")
            .add("verificationCode",  verificationCode.getValue())
            .add("usedFor",usedforvalue.getValue())
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

    /**
     * 检查验证码并注册
     */
    public void checkVerificationCode(int flowType) {
        Parameter parameter = getParameter(flowType);
        WaitDialog waitDialog = showWait();
        N.API(OpenIMService.class).checkVerificationCode(parameter.buildJsonBody())

            .compose(N.IOMain())
            .subscribe(new NetObserver<ResponseBody>(getContext()) {
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
                        Base<LoginCertificate> loginCertificate = GsonHel.dataObject(body, LoginCertificate.class);
                        if (loginCertificate.errCode != 0) {
                            IView.onErr(loginCertificate.errMsg);
                            return;
                        }
                        IView.onSucce("checkVerificationCode");

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
    public void getVerificationCode(int flowType) {
        System.out.println("Joy bug :");
        Parameter parameter = getParameter(flowType);
        WaitDialog waitDialog = showWait();
        N.API(OpenIMService.class).getVerificationCode(parameter.buildJsonBody())
            .compose(N.IOMain())
            .subscribe(new NetObserver<ResponseBody>(getContext()) {
                @Override
                public void onSuccess(ResponseBody o) {
                    String body = null;
                    try {
                        body = o.string();
                        Base<String> data = GsonHel.fromJson(body, Base.class);
                        System.out.println("Joy bug : on success"+data.errCode + " "+data.errMsg);
                        if (data.errCode != 0 && data.errCode != 10008) {
                            IView.onErr(data.errMsg);
                            return;
                        }
                        IView.onResendCode(null);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onComplete() {
                    super.onComplete();
                    waitDialog.dismiss();
                }

                @Override
                protected void onFailure(Throwable e) {
                    IView.onErr(e.getMessage());
                }
            });

    }

    public interface ViewAction extends IView {

        void onErr(String msg);
        void onSucce(Object o);
        void onResendCode(Object o);
    }

}
